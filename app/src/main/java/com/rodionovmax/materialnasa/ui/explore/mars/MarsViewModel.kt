package com.rodionovmax.materialnasa.ui.explore.mars

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.domain.FetchMarsPhotosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
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

    private val _roverPhoto: MutableStateFlow<MarsPhoto?> = MutableStateFlow(null)
    val roverPhoto: StateFlow<MarsPhoto?> = _roverPhoto.asStateFlow()

    init {
        savedStateHandle.get<String>(STATE_CAMERA)?.let { cam ->
            setCamera(cam)
        }
        savedStateHandle.get<String>(STATE_DATE_PICKER)?.let { date ->
            setDate(date)
        }
    }

//    fun getPhotosWithPaging() {
//        _uiState.value = MarsUiState.Loading
//        viewModelScope.launch {
//            _uiState.value = when (val result =
//                fetchMarsPhotosUseCase.getPhotosWithPaging(_cameraState.value, _dateState.value)) {
//                is Result.Success -> MarsUiState.Success(data = result.data.last())
//                is Result.Error -> MarsUiState.Error(Throwable(result.throwable))
//            }
//        }
//    }

    fun fetchMarsPhotos() {
        _uiState.value = MarsUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = fetchMarsPhotosUseCase(_cameraState.value, _dateState.value)) {
                is Result.Success -> MarsUiState.Success(data = result.data).also {
                    saveMarsPhotosToDb(result.data)
                }
                is Result.Error -> MarsUiState.Error(Throwable(result.throwable))
            }
        }
    }

    private suspend fun saveMarsPhotosToDb(photos: List<MarsPhoto>) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchMarsPhotosUseCase.cleanRoverGalleryTable()
            fetchMarsPhotosUseCase.savePhotosToDb(photos)
        }
    }

    fun onRoverPhotoClicked(photo: MarsPhoto) {
        _roverPhoto.value = photo
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