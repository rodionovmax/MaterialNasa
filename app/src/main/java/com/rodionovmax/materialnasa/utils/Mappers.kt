package com.rodionovmax.materialnasa.utils

import com.rodionovmax.materialnasa.data.local.GalleryPodEntity
import com.rodionovmax.materialnasa.data.network.model.PodDto
import com.rodionovmax.materialnasa.data.model.Pod

fun Pod.asEntity(): GalleryPodEntity = GalleryPodEntity(
    id = null,
    imgUrl = url,
    title = title,
    description = description,
    copyright = copyright,
    date = date,
    isSaved = isSaved
)

fun PodDto.asDomainPod() = Pod(
    copyright = copyright ?: "",
    date = date,
    description = explanation,
    hdUrl = hdUrl ?: "",
    mediaType = mediaType,
    title = title,
    url = url,
    isSaved = false
)

fun GalleryPodEntity.asDomainPod() = Pod(
    copyright = copyright ?: "",
    date = date,
    description = description,
    hdUrl = "",
    mediaType = "",
    title = title,
    url = imgUrl,
    isSaved = isSaved
)


/*
data class GalleryPodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val imgUrl: String,
    val title: String,
    val copyright: String?,
    val date: String,
)
* */

/*
data class GalleryPod(
    val id: Int,
    val imgUrl: String,
    val title: String,
    val copyright: String?,
    val date: String
) : Parcelable
* */

/*
data class Pod (
    val copyright: String?,
    val date: String,
    val description: String,
    val hdUrl: String?,
    val mediaType: String,
    val title: String,
    val url: String,
    val isSaved: Boolean
) : Parcelable
* */