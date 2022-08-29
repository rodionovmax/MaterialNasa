package com.rodionovmax.materialnasa.ui.pod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rodionovmax.materialnasa.data.RemoteRepoImpl
import com.rodionovmax.materialnasa.domain.model.Pod
import com.rodionovmax.materialnasa.domain.repo.RemoteRepo
import com.rodionovmax.materialnasa.utils.getDateWithOffset

class PodViewModel(
    private val remoteRepo: RemoteRepo = RemoteRepoImpl()
) : ViewModel() {

    val podLiveData: LiveData<Pod> get() = _podLiveData
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val _podLiveData: MutableLiveData<Pod> = MutableLiveData()
    private val _errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    private val _progressLiveData: MutableLiveData<Boolean> = MutableLiveData()

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
    }
}