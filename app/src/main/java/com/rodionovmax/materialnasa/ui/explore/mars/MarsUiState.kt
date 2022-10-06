package com.rodionovmax.materialnasa.ui.explore.mars

import com.rodionovmax.materialnasa.data.model.MarsPhoto

sealed class MarsUiState {
    object Loading: MarsUiState()
    data class Success(val marsPhotos: List<MarsPhoto>): MarsUiState()
    data class Error(var error: Throwable): MarsUiState()
}