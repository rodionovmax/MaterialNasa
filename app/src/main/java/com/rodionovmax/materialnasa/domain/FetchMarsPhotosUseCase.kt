package com.rodionovmax.materialnasa.domain

import androidx.paging.PagingData
import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.data.repo.RemoteRepo
import kotlinx.coroutines.flow.Flow

class FetchMarsPhotosUseCase(
    private val remoteRepo: RemoteRepo,
    private val localRepo: LocalRepo
) {
    suspend operator fun invoke(
        camera: String,
        earthDate: String
    ): Result<List<MarsPhoto>> {
        return if (camera == "ALL") {
            remoteRepo.getAllPhotos(earthDate)
        } else remoteRepo.getPhotosForCamera(camera, earthDate)
    }

    suspend fun savePhotosToDb(photos: List<MarsPhoto>) {
        localRepo.saveRoverPhotosToDb(photos)
    }

    suspend fun cleanRoverGalleryTable() {
        localRepo.cleanRoverGalleryTable()
    }

    suspend fun getPhotosWithPaging(camera: String, earthDate: String): Result<Flow<PagingData<MarsPhoto>>> {
        return remoteRepo.getMarsPhotosStream(camera, earthDate)
    }

}