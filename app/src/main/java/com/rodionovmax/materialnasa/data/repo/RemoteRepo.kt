package com.rodionovmax.materialnasa.data.repo

import com.rodionovmax.materialnasa.data.model.Pod

interface RemoteRepo {

    fun getPictureOfTheDay(
        date: String,
        onSuccess: (Pod) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    )
}