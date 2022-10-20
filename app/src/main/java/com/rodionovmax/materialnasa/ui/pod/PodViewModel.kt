package com.rodionovmax.materialnasa.ui.pod

import androidx.lifecycle.*
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.data.repo.RemoteRepo
import com.rodionovmax.materialnasa.ui.explore.mars.STATE_CAMERA
import com.rodionovmax.materialnasa.ui.explore.mars.STATE_DATE_PICKER
import com.rodionovmax.materialnasa.utils.SingleEventLiveData
import com.rodionovmax.materialnasa.utils.getDateWithOffset
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


const val FOREGROUND_COLOR = "foregroundColor"
const val BACKGROUND_COLOR = "backgroundColor"
const val TITLE = "title"
const val IS_CHECKED = "isChecked"


class PodViewModel(
    private val remoteRepo: RemoteRepo,
    private val localRepo: LocalRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val podLiveData: LiveData<Pod> get() = _podLiveData
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData
    val foregroundColor: LiveData<Int> get() = _foregroundColor
    val backgroundColor: LiveData<Int> get() = _backgroundColor
    val title: LiveData<CharSequence> get() = _title

    private val _podLiveData: MutableLiveData<Pod> = MutableLiveData()
    private val _errorLiveData: MutableLiveData<Throwable> = SingleEventLiveData()
    private val _progressLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val _foregroundColor:MutableLiveData<Int> = MutableLiveData()
    private val _backgroundColor:MutableLiveData<Int> = MutableLiveData()
    private val _title:MutableLiveData<CharSequence> = MutableLiveData()

    init {
        savedStateHandle.get<Int>(FOREGROUND_COLOR)?.let { foregroundColor ->
            setForegroundColor(foregroundColor)
        }
        savedStateHandle.get<Int>(BACKGROUND_COLOR)?.let { backgroundColor ->
            setBackgroundColor(backgroundColor)
        }
        savedStateHandle.get<String>(TITLE)?.let { title ->
            saveTitle(title)
        }
    }

    fun setForegroundColor(foregroundColor: Int) {
        _foregroundColor.value = foregroundColor
        savedStateHandle[FOREGROUND_COLOR] = foregroundColor
    }

    fun setBackgroundColor(backgroundColor: Int) {
        _backgroundColor.value = backgroundColor
        savedStateHandle[BACKGROUND_COLOR] = backgroundColor
    }

    fun saveTitle(title: CharSequence) {
        _title.value = title
        savedStateHandle[TITLE] = title
    }

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
