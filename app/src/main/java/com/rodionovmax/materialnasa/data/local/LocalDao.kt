package com.rodionovmax.materialnasa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToPodGallery(galleryEntity: GalleryPodEntity)

    @Query("SELECT * FROM favorite_pod ORDER BY id DESC")
    fun getAllFromGallery(): List<GalleryPodEntity>

    @Query("SELECT * FROM favorite_pod WHERE date == :selectedDate")
    fun getPodByDate(selectedDate: String): GalleryPodEntity
}