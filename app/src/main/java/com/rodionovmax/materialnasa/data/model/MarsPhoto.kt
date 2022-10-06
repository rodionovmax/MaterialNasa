package com.rodionovmax.materialnasa.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MarsPhoto(
    val id: Int,
    val sol: Int,
    val cameraId: Int,
    val cameraName: String,
    val roverId: Int,
    val imgSrc: String,
    val earthDate: String,
): Parcelable
