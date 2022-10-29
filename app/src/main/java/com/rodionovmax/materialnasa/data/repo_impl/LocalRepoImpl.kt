package com.rodionovmax.materialnasa.data.repo_impl

import com.rodionovmax.materialnasa.data.Result
import com.rodionovmax.materialnasa.data.local.*
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.utils.asDomainPod
import com.rodionovmax.materialnasa.utils.asEntity
import com.rodionovmax.materialnasa.utils.asEntityRoverPhotos
import kotlinx.coroutines.*

class LocalRepoImpl(
    private val localDataSource: LocalDao,
    private val roverDataSource: RoverGalleryDao
) : LocalRepo {

    override suspend fun addPodToGallery(pod: Pod) {
        withContext(Dispatchers.IO) {
            val gallery: List<GalleryPodEntity> = localDataSource.getAllFromGallery()
            val dates = gallery.map { it.date }
            if (pod.date !in dates) {
                pod.isSaved = true
                localDataSource.incrementGalleryPositions()
                localDataSource.addToPodGallery(pod.asEntity())

            }
        }
    }

    override fun getPodByDate(date: String): Pod? {
        var pod: GalleryPodEntity? = null
        runBlocking {
            launch(Dispatchers.Default) {
                withContext(Dispatchers.IO) {
                    pod = localDataSource.getPodByDate(date)
                }
            }
        }
        return pod?.asDomainPod()
    }

    override fun getAllFromGallery(): List<Pod> {
        var gallery = listOf<Pod>()
        runBlocking {
            launch(Dispatchers.Default) {
                withContext(Dispatchers.IO) {
                    gallery = localDataSource.getAllFromGallery().map { it.asDomainPod() }
                }
            }
        }
        return gallery
    }

    override suspend fun removeItemFromGallery(pod: Pod) {
        withContext(Dispatchers.IO) {
            pod.date?.let { localDataSource.updateItemPositionsInGalleryWhenDeletingPicture(it) }
            pod.date?.let { localDataSource.deletePodByDate(it) }
        }
    }

    override suspend fun updateGalleryItemPositions(posFrom: Int, posTo: Int, currentItem: Pod) {
        withContext(Dispatchers.IO) {
            if (posFrom < posTo) {
                localDataSource.adjustPositionsIfMovedDown(posFrom, posTo)
                currentItem.date?.let { localDataSource.updatePositionOfMovedItem(posTo, it) }
            } else {
                localDataSource.adjustPositionsIfMovedUp(posFrom, posTo)
                currentItem.date?.let { localDataSource.updatePositionOfMovedItem(posTo, it) }
            }
        }
    }

    override suspend fun saveRoverPhotosToDb(photos: List<MarsPhoto>) {
        val dbPhotos = asEntityRoverPhotos(photos)
        roverDataSource.insertRoverPhotos(dbPhotos)
    }

    override suspend fun getRoverImage(adapterPosition: Int): RoverPhotoEntity {
        return roverDataSource.getRoverPhoto(adapterPosition)
    }

    override suspend fun cleanRoverGalleryTable() {
        roverDataSource.deleteAll()
    }
}