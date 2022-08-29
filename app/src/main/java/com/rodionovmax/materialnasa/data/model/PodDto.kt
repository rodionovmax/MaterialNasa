package com.rodionovmax.materialnasa.data.model

import com.google.gson.annotations.SerializedName
import com.rodionovmax.materialnasa.domain.model.Pod

data class PodDto(
    @SerializedName("copyright") val copyright: String?,
    @SerializedName("date") val date: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("hdurl") val hdUrl: String?,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("service_version") val serviceVersion: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)

fun PodDto.asDomainPod() = Pod(
    copyright = copyright ?: "",
    date = date,
    description = explanation,
    hdUrl = hdUrl ?: "",
    mediaType = mediaType,
    title = title,
    url = url
)