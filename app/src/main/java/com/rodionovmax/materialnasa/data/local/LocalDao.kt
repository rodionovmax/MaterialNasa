package com.rodionovmax.materialnasa.data.local

import androidx.room.*
import com.rodionovmax.materialnasa.domain.model.Pod

@Dao
interface LocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToPodGallery(galleryEntity: GalleryPodEntity)

    @Query("SELECT * FROM favorite_pod ORDER BY id DESC")
    fun getAllFromGallery(): List<GalleryPodEntity>

    @Query("SELECT * FROM favorite_pod WHERE date == :selectedDate")
    fun getPodByDate(selectedDate: String): GalleryPodEntity

    @Delete
    fun deletePod(galleryEntity: GalleryPodEntity)

    @Query("DELETE FROM favorite_pod WHERE date = :podDate")
    fun deletePodByDate(podDate: String)
}