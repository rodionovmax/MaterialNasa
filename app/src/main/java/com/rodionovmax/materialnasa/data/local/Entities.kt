package com.rodionovmax.materialnasa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_pod")
data class GalleryPodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val imgUrl: String,
    val title: String,
    val description: String,
    val copyright: String?,
    val date: String,
    val isSaved: Boolean,
    val position: Int?
)

@Entity(tableName = "rover_photos")
data class RoverPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val camera: String,
    val earthDate: String
)