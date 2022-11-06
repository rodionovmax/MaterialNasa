package com.rodionovmax.materialnasa.data.repo

import androidx.paging.PagingData
import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.data.model.Pod
import kotlinx.coroutines.flow.Flow

interface RemoteRepo {

    fun getPictureOfTheDay(
        date: String,
        onSuccess: (Pod) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    )

    suspend fun getPhotosForCamera(
        camera: String,
        earthDate: String
    ): Result<List<MarsPhoto>>

    suspend fun getAllPhotos(
        earthDate: String
    ): Result<List<MarsPhoto>>

    suspend fun getMarsPhotosStream(camera: String, earthDate: String): Result<Flow<PagingData<MarsPhoto>>>
}
