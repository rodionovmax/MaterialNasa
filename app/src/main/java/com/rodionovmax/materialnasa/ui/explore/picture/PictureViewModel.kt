package com.rodionovmax.materialnasa.ui.explore.picture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PictureViewModel(
    private val localRepo: LocalRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<PictureUiState>(PictureUiState.Empty)
    val uiState: StateFlow<PictureUiState> = _uiState.asStateFlow()

    fun loadPicture(adapterPosition: Int) {
        _uiState.value = PictureUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
//            _uiState.value = when (val result = localRepo.getRoverImage(adapterPosition)) {
//                is Result.Success -> PictureUiState.Success(data = result.data)
//                is Result.Error -> PictureUiState.Error(Throwable(result.throwable))
//            }
        }
    }


}

