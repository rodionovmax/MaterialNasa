package com.rodionovmax.materialnasa.ui.explore.mars

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.domain.FetchMarsPhotosUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

const val STATE_CAMERA = "camera"
const val STATE_DATE_PICKER = "date"

class MarsViewModel(
    private val fetchMarsPhotosUseCase: FetchMarsPhotosUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarsUiState>(MarsUiState.Empty)
    val uiState: StateFlow<MarsUiState> = _uiState.asStateFlow()

    private val _cameraState: MutableStateFlow<String> = MutableStateFlow("")
    val cameraState: StateFlow<String> = _cameraState.asStateFlow()

    private val _dateState: MutableStateFlow<String> = MutableStateFlow("")
    val dateState: StateFlow<String> = _dateState.asStateFlow()

    init {
        savedStateHandle.get<String>(STATE_CAMERA)?.let { cam ->
            setCamera(cam)
        }
        savedStateHandle.get<String>(STATE_DATE_PICKER)?.let { date ->
            setDate(date)
        }
    }

    fun fetchMarsPhotos() {
        _uiState.value = MarsUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = fetchMarsPhotosUseCase(_cameraState.value, _dateState.value)) {
                is Result.Success -> MarsUiState.Success(data = result.data)
                is Result.Error -> MarsUiState.Error(Throwable(result.throwable))
            }
        }
    }

    fun setCamera(camera: String) {
        _cameraState.value = camera
        savedStateHandle[STATE_CAMERA] = camera
    }

    fun setDate(date: String) {
        _dateState.value = date
        savedStateHandle[STATE_DATE_PICKER] = date
    }

    enum class ToastEvent {
        CAMERA_SELECTED,
        SELECT_CAMERA,
        SELECT_DATE
    }

    sealed class ToastState {
        data class CameraSelected(val message: String): ToastState()
        data class SelectCamera(val message: String): ToastState()
        data class SelectDate(val message: String): ToastState()
    }

    private val toastChannel = Channel<ToastState>()
    val toastEventFlow = toastChannel.receiveAsFlow()

    fun triggerToast(toastEvent: ToastEvent) = viewModelScope.launch {
        when(toastEvent) {
            ToastEvent.CAMERA_SELECTED -> toastChannel.send(ToastState.CameraSelected("${_cameraState.value} was selected"))
            ToastEvent.SELECT_CAMERA -> toastChannel.send(ToastState.SelectCamera("You need to select camera..."))
            ToastEvent.SELECT_DATE -> toastChannel.send(ToastState.SelectDate("Select date first!"))
        }
    }
}