package com.rodionovmax.materialnasa.ui.pod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.data.repo.RemoteRepo
import com.rodionovmax.materialnasa.utils.SingleEventLiveData
import com.rodionovmax.materialnasa.utils.getDateWithOffset
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PodViewModel(
    private val remoteRepo: RemoteRepo,
    private val localRepo: LocalRepo
) : ViewModel() {

    val podLiveData: LiveData<Pod> get() = _podLiveData
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _podLiveData: MutableLiveData<Pod> = MutableLiveData()
    private val _errorLiveData: MutableLiveData<Throwable> = SingleEventLiveData()
    private val _progressLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private fun loadPodFromInternet(date: String) {
        _progressLiveData.postValue(true)
        remoteRepo.getPictureOfTheDay(
            date,
            onSuccess = {
                _progressLiveData.postValue(false)
                _podLiveData.postValue(it)
            },
            onError = {
                _progressLiveData.postValue(false)
                _errorLiveData.postValue(it)
            }
        )
    }

    fun setDateOnChipClicked(selectedDay: Int) {
        var date = ""
        when(selectedDay) {
            0 -> date = getDateWithOffset(0)
            1 -> date = getDateWithOffset(1)
            2 -> date = getDateWithOffset(2)
        }
        if (isPodSavedInGallery(date)) {
            getPodFromDatabase(date)
        } else {
            loadPodFromInternet(date)
        }
    }

    private fun getPodFromDatabase(date: String) {
        _progressLiveData.postValue(true)
        val podFromDb = localRepo.getPodByDate(date)
        podFromDb?.let {
            _progressLiveData.postValue(false)
            _podLiveData.postValue(it)
        }
    }

    fun savePodToGallery(pod: Pod) {
        viewModelScope.launch {
            localRepo.addPodToGallery(pod)
        }
    }

    private fun isPodSavedInGallery(date: String): Boolean {
        val pod = localRepo.getPodByDate(date)
        return pod?.isSaved ?: false
    }

    sealed class PodEvent() {
        data class PodToast(val message: String): PodEvent()
    }

    private val eventChannel = Channel<PodEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun triggerEvent(chipName: String) = viewModelScope.launch {
        eventChannel.send(PodEvent.PodToast("$chipName was selected"))
    }
}
