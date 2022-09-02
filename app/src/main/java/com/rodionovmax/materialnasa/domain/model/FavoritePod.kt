package com.rodionovmax.materialnasa.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoritePod(
    val imgUrl: String,
    val title: String,
    val copyright: String?,
    val date: String
) : Parcelable
