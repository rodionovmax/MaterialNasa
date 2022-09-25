package com.rodionovmax.materialnasa.data.repo

import com.rodionovmax.materialnasa.data.model.Pod

interface LocalRepo {

    suspend fun addPodToGallery(pod: Pod)
    fun getPodByDate(date: String): Pod?
    fun getAllFromGallery(): List<Pod>
    suspend fun removeItemFromGallery(pod: Pod)
}