package com.rodionovmax.materialnasa.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.utils.SingleEventLiveData
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val localRepo: LocalRepo
) : ViewModel() {

    val galleryLiveData: LiveData<List<Pod>> get() = _galleryLiveData
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val _galleryLiveData: MutableLiveData<List<Pod>> = MutableLiveData()
    private val _progressLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val _errorLiveData: MutableLiveData<Throwable> = SingleEventLiveData()

    fun getGallery() {
        _progressLiveData.postValue(true)
        val gallery: List<Pod> = localRepo.getAllFromGallery()
        _progressLiveData.postValue(false)
        _galleryLiveData.postValue(gallery)
    }

    fun removeFromGallery(pod: Pod) {
        _progressLiveData.postValue(true)
        viewModelScope.launch {
            localRepo.removeItemFromGallery(pod)
        }
        _progressLiveData.postValue(false)
    }

    fun updateGalleryItemPositionsInDb(posFrom: Int, posTo: Int, currentItem: Pod) {
        viewModelScope.launch {
            localRepo.updateGalleryItemPositions(posFrom, posTo, currentItem)
        }
    }

}