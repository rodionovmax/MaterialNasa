package com.rodionovmax.materialnasa.domain.impl

import com.rodionovmax.materialnasa.data.local.GalleryPodEntity
import com.rodionovmax.materialnasa.data.local.LocalDao
import com.rodionovmax.materialnasa.domain.model.Pod
import com.rodionovmax.materialnasa.domain.repo.LocalRepo
import com.rodionovmax.materialnasa.utils.asDomainPod
import com.rodionovmax.materialnasa.utils.asEntity
import kotlinx.coroutines.*

class LocalRepoImpl(private val localDataSource: LocalDao) : LocalRepo {

    override fun addPodToGallery(pod: Pod) {
        GlobalScope.launch(Dispatchers.IO) {
            val gallery: List<GalleryPodEntity> = localDataSource.getAllFromGallery()
            val dates = mutableListOf<String>()
            for (picture in gallery) {
                dates.add(picture.date)
            }
            if (pod.date !in dates) {
                pod.isSaved = true
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


}