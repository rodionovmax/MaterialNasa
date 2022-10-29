package com.rodionovmax.materialnasa.data.repo

import com.rodionovmax.materialnasa.data.local.RoverPhotoEntity
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.data.model.Pod

interface LocalRepo {

    suspend fun addPodToGallery(pod: Pod)
    fun getPodByDate(date: String): Pod?
    fun getAllFromGallery(): List<Pod>
    suspend fun removeItemFromGallery(pod: Pod)
    suspend fun updateGalleryItemPositions(posFrom: Int, posTo: Int, currentItem: Pod)

    suspend fun saveRoverPhotosToDb(photos: List<MarsPhoto>)
    suspend fun getRoverImage(adapterPosition: Int): RoverPhotoEntity
    suspend fun cleanRoverGalleryTable()
}