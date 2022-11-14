package com.rodionovmax.materialnasa.data.local

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite_pod")
data class GalleryPodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val imgUrl: String?,
    val title: String?,
    val description: String?,
    val copyright: String?,
    val date: String?,
    val isSaved: Boolean,
    val position: Int?,
    val source: Int,
    val name: String?,
    val bmp: Bitmap?,
    val uri: Uri?
)

@Entity(tableName = "rover_photos")
data class RoverPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val camera: String,
    val earthDate: String
)

@Entity(tableName = "camera_photo")
data class CameraPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val bmp: Bitmap
)

@Entity(tableName = "search_results")
data class SearchResultEntity(
    @PrimaryKey @field:SerializedName("id") val id: Long = 0,
    @field:SerializedName("title") val title: String = "",
    @field:SerializedName("keywords") val keywords: String = "",
    @field:SerializedName("description") val description: String? = "",
    @field:SerializedName("imgUrl") val imgUrl: String = "",
    @field:SerializedName("created") val created: String = "",
)
