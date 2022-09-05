package com.rodionovmax.materialnasa.domain.repo

import com.rodionovmax.materialnasa.domain.model.Pod

interface LocalRepo {

    fun addPodToGallery(pod: Pod)
    fun getPodByDate(date: String): Pod?
    fun getAllFromGallery(): List<Pod>
    fun removeItemFromGallery(pod: Pod)
}