package com.rodionovmax.materialnasa.ui.explore.mars

import androidx.lifecycle.*
import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.domain.FetchMarsPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val CAMERA_FHAZ = "FHAZ"

class MarsViewModel(
    private val fetchMarsPhotosUseCase: FetchMarsPhotosUseCase
) : ViewModel() {
    private val _marsPhotosLiveData: MutableLiveData<List<MarsPhoto>> = MutableLiveData()
    val marsPhotosLiveData: LiveData<List<MarsPhoto>> get() = _marsPhotosLiveData

    private val _uiState: MutableStateFlow<MarsUiState> = MutableStateFlow(MarsUiState.Loading)
    val uiState: StateFlow<MarsUiState> get() = _uiState.asStateFlow()

    private val _selectedCamera: MutableStateFlow<String> = MutableStateFlow(CAMERA_FHAZ)
    val selectedCamera: StateFlow<String> get() = _selectedCamera.asStateFlow()

    private val _selectedDate: MutableStateFlow<String> = MutableStateFlow("")
    val selectedDate: StateFlow<String> get() = _selectedDate.asStateFlow()

    fun fetchMarsPhotos(camera: String, earthDate: String) {
        viewModelScope.launch {
            _uiState.value = MarsUiState.Loading
            _uiState.value = when(val result = fetchMarsPhotosUseCase(camera, earthDate)) {
                is Result.Success -> MarsUiState.Success(marsPhotos = result.data)
                is Result.Error -> MarsUiState.Error(Throwable(result.throwable))
            }
        }
    }

    fun selectCamera(camera: String) {
        _selectedCamera.value = camera
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }


}