package com.rodionovmax.materialnasa.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pod (
    val copyright: String?,
    val date: String?,
    val description: String?,
    val hdUrl: String?,
    val mediaType: String?,
    val title: String?,
    val url: String?,
    var isSaved: Boolean,
    val source: Int,
    val name: String?,
    val bmp: Bitmap?
) : Parcelable