package com.rodionovmax.materialnasa.ui.explore.mars

import androidx.paging.PagingData
import com.rodionovmax.materialnasa.data.model.MarsPhoto

sealed class MarsUiState {
    object Empty:MarsUiState()
    object Loading: MarsUiState()
    data class Success(val data: List<MarsPhoto>): MarsUiState()
//    data class Success(val data: PagingData<MarsPhoto>): MarsUiState()
    data class Error(var error: Throwable): MarsUiState()
}