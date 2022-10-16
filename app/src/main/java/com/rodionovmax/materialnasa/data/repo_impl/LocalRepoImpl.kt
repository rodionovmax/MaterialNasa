package com.rodionovmax.materialnasa.data.repo_impl

import com.rodionovmax.materialnasa.data.local.GalleryPodEntity
import com.rodionovmax.materialnasa.data.local.LocalDao
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.data.repo.LocalRepo
import com.rodionovmax.materialnasa.utils.asDomainPod
import com.rodionovmax.materialnasa.utils.asEntity
import kotlinx.coroutines.*

class LocalRepoImpl(private val localDataSource: LocalDao) : LocalRepo {

    override suspend fun addPodToGallery(pod: Pod) {
        withContext(Dispatchers.IO) {
            val gallery: List<GalleryPodEntity> = localDataSource.getAllFromGallery()
            val dates = gallery.map { it.date }
            if (pod.date !in dates) {
                pod.isSaved = true
                localDataSource.addToPodGallery(pod.asEntity())
                localDataSource.incrementGalleryPosition()
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
            localDataSource.updateItemPositionsInGalleryWhenDeletingPicture(pod.date)
            localDataSource.deletePodByDate(pod.date)
        }
    }
}