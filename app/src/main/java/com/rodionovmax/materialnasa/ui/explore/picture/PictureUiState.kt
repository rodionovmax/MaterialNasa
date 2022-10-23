package com.rodionovmax.materialnasa.ui.explore.picture

import com.rodionovmax.materialnasa.data.local.RoverPhotoEntity

sealed class PictureUiState {
    object Empty: PictureUiState()
    object Loading: PictureUiState()
    data class Success(val data: RoverPhotoEntity): PictureUiState()
    data class Error(val error: Throwable): PictureUiState()
}


