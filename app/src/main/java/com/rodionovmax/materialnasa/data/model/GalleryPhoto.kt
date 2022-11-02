package com.rodionovmax.materialnasa.data.model

import android.graphics.Bitmap
import android.net.Uri

data class GalleryPhoto(
    val contentUri: Uri,
    val bitmap: Bitmap
)
