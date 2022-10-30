package com.rodionovmax.materialnasa.data.local

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

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