package com.rodionovmax.materialnasa.ui.pod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionovmax.materialnasa.data.RemoteRepoImpl
import com.rodionovmax.materialnasa.domain.model.Pod
import com.rodionovmax.materialnasa.domain.repo.RemoteRepo
import com.rodionovmax.materialnasa.utils.SingleEventLiveData
import com.rodionovmax.materialnasa.utils.getDateWithOffset
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PodViewModel(
    private val remoteRepo: RemoteRepo = RemoteRepoImpl()
) : ViewModel() {

    val podLiveData: LiveData<Pod> get() = _podLiveData
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData
    val displayToastLiveData: LiveData<Boolean> get() = _displayToastLiveData
    val chipIdLiveData: LiveData<Int> get() = _chipIdLiveData

    private val _podLiveData: MutableLiveData<Pod> = MutableLiveData()
    private val _errorLiveData: MutableLiveData<Throwable> = SingleEventLiveData()
    private val _progressLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val _displayToastLiveData: MutableLiveData<Boolean> = SingleEventLiveData()
    private val _chipIdLiveData: MutableLiveData<Int> = MutableLiveData()

    private fun loadData(date: String) {
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
        loadData(date)
        showToast(true)
    }

    fun showToast(toShow: Boolean) {
        _displayToastLiveData.postValue(toShow)
    }

    fun saveChipId(chipId: Int): Int {
        _chipIdLiveData.postValue(chipId)
        return chipId
    }

    sealed class PodEvent() {
        data class ToastEvent(val message: String): PodEvent()
    }

    private val eventChannel = Channel<PodEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun triggerEvent(chipName: String) = viewModelScope.launch {
        eventChannel.send(PodEvent.ToastEvent("$chipName was selected"))
    }
}
