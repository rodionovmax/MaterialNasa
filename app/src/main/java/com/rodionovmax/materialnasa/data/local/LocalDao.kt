package com.rodionovmax.materialnasa.data.local

import androidx.room.*

@Dao
interface LocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToPodGallery(galleryEntity: GalleryPodEntity)

    @Query("update favorite_pod set position=(select max(position) from favorite_pod)+1 where id=(select max(id) from favorite_pod)")
    fun incrementGalleryPosition()

    @Query("SELECT * FROM favorite_pod ORDER BY position DESC")
    fun getAllFromGallery(): List<GalleryPodEntity>

    @Query("SELECT * FROM favorite_pod WHERE date == :selectedDate")
    fun getPodByDate(selectedDate: String): GalleryPodEntity

    @Delete
    fun deletePod(galleryEntity: GalleryPodEntity)

    @Query("update favorite_pod set position=position-1 where position>(select position from favorite_pod where date=:podDate)")
    fun updateItemPositionsInGalleryWhenDeletingPicture(podDate: String)

    @Query("DELETE FROM favorite_pod WHERE date = :podDate")
    fun deletePodByDate(podDate: String)
}