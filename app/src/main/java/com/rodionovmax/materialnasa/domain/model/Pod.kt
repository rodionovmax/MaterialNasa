package com.rodionovmax.materialnasa.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pod (
    val copyright: String?,
    val date: String,
    val description: String,
    val hdUrl: String?,
    val mediaType: String,
    val title: String,
    val url: String
) : Parcelable