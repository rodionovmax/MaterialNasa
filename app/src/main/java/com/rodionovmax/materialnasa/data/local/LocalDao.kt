package com.rodionovmax.materialnasa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rodionovmax.materialnasa.domain.model.Pod

@Dao
interface LocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToPodGallery(galleryEntity: GalleryPodEntity)

    @Query("SELECT * FROM favorite_pod")
    fun getAllFromGallery(): List<GalleryPodEntity>

    @Query("SELECT * FROM favorite_pod WHERE date == :selectedDate")
    fun getPodByDate(selectedDate: String): GalleryPodEntity
}