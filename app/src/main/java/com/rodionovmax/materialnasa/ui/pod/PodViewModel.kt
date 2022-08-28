package com.rodionovmax.materialnasa.ui.pod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PodViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is picture of the day Fragment"
    }
    val text: LiveData<String> = _text
}