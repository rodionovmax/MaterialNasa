package com.rodionovmax.materialnasa.ui.pod

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.data.repo.RemoteRepo

class PodViewModelFactory constructor(
    private val owner: SavedStateRegistryOwner,
    private val remoteRepo: RemoteRepo,
    private val localRepo: LocalRepo,
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        state: SavedStateHandle
    ) = PodViewModel(remoteRepo, localRepo, state) as T
}

