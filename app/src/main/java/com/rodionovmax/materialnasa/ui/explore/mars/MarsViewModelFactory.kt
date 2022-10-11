package com.rodionovmax.materialnasa.ui.explore.mars

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.rodionovmax.materialnasa.domain.FetchMarsPhotosUseCase


class MarsViewModelFactory constructor(
    private val owner: SavedStateRegistryOwner,
    private val fetchMarsPhotosUseCase: FetchMarsPhotosUseCase,
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        state: SavedStateHandle
    ) = MarsViewModel(fetchMarsPhotosUseCase, state) as T
}






