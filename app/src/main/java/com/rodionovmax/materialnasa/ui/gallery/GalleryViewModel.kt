package com.rodionovmax.materialnasa.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rodionovmax.materialnasa.domain.model.Pod
import com.rodionovmax.materialnasa.domain.repo.LocalRepo
import com.rodionovmax.materialnasa.utils.SingleEventLiveData

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
        localRepo.removeItemFromGallery(pod)
        _progressLiveData.postValue(false)
    }

}