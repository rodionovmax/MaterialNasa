package com.rodionovmax.materialnasa.domain.repo

import com.rodionovmax.materialnasa.domain.model.Pod

interface RemoteRepo {

    fun getPictureOfTheDay(
        date: String,
        onSuccess: (Pod) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    )
}