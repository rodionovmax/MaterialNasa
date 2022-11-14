package com.rodionovmax.materialnasa.utils

import com.rodionovmax.materialnasa.data.local.CameraPhotoEntity
import com.rodionovmax.materialnasa.data.local.GalleryPodEntity
import com.rodionovmax.materialnasa.data.local.RoverPhotoEntity
import com.rodionovmax.materialnasa.data.model.*
import com.rodionovmax.materialnasa.data.network.model.ArticleDto
import com.rodionovmax.materialnasa.data.network.model.ItemsDto
import com.rodionovmax.materialnasa.data.network.model.MarsResultsDto
import com.rodionovmax.materialnasa.data.network.model.PodDto

fun Pod.asEntity(): GalleryPodEntity = GalleryPodEntity(
    id = null,
    imgUrl = url,
    title = title,
    description = description,
    copyright = copyright,
    date = date,
    isSaved = isSaved,
    position = 0,
    source = source,
    name = name,
    bmp = bmp,
    uri = uri
)

fun PodDto.asDomainPod() = Pod(
    copyright = copyright ?: "",
    date = date,
    description = explanation,
    hdUrl = hdUrl ?: "",
    mediaType = mediaType,
    title = title,
    url = url,
    isSaved = false,
    source = 1,
    name = null,
    bmp = null,
    uri = null
)

fun GalleryPodEntity.asDomainPod() = Pod(
    copyright = copyright ?: "",
    date = date ?: "",
    description = description ?: "",
    hdUrl = "",
    mediaType = "",
    title = title ?: "",
    url = imgUrl ?: "",
    isSaved = isSaved,
    source = source,
    name = name,
    bmp = bmp,
    uri = uri
)

fun asDomainMarsPhotos(marsResultsDto: MarsResultsDto): List<MarsPhoto> {
    return marsResultsDto.results.map {
        MarsPhoto(
            id = it.id,
            sol = it.sol,
            cameraId = it.camera.id,
            cameraName = it.camera.cameraName,
            roverId = it.rover.id,
            imgSrc = it.imageSrc,
            earthDate = it.earthDate
        )
    }
}

fun asEntityRoverPhotos(marsPhotos: List<MarsPhoto>): List<RoverPhotoEntity> {
    return marsPhotos.map {
        RoverPhotoEntity(
            id = null,
            camera = it.cameraName,
            earthDate = it.earthDate
        )
    }
}

fun CameraPhotoEntity.toPod(): Pod = Pod(
    copyright = "Max Rodionov",
    date = getTimestamp(),
    description = null,
    hdUrl = null,
    mediaType = null,
    title = "Photo from internal storage",
    url = null,
    isSaved = true,
    source = 2,
    name = name,
    bmp = bmp,
    uri = null
)

fun SharedStoragePhoto.toPod(): Pod = Pod(
    copyright = "Google",
    date = getTimestamp(),
    description = null,
    hdUrl = null,
    mediaType = null,
    title = "Picture from external storage",
    url = null,
    isSaved = true,
    source = 3,
    name = name,
    bmp = null,
    uri = contentUri
)

fun GalleryPhoto.toPod(): Pod = Pod(
    copyright = "Gallery",
    date = getTimestamp(),
    description = null,
    hdUrl = null,
    mediaType = null,
    title = "Picture from phone gallery",
    url = null,
    isSaved = true,
    source = 4,
    name = null,
    bmp = bitmap,
    uri = null
)

fun asDomainSearchResults(itemsDto: List<ItemsDto?>): List<SearchResult> {
    return itemsDto.map {
        it?.let {
            SearchResult(
                id = itemsDto.indexOf(it),
                title = it.data[0].title,
                keywords = it.data[0].keywords,
                description = it.data[0].description,
                imgUrl = it.links?.get(0)?.url ?: ""
            )
        } ?: SearchResult()
    }
}

fun ArticleDto.toArticle(): NewsArticle = NewsArticle(
    id = null,
    name = source?.name ?: "",
    author = author ?: "",
    title = title ?: "",
    description = description ?: "",
    url = url ?: "",
    urlToImage = urlToImg ?: "",
    publishedAt = publishedAt ?: "",
    content = content ?: ""
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