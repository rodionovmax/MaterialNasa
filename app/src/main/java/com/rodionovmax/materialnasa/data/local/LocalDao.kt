package com.rodionovmax.materialnasa.data.local

import androidx.room.*

@Dao
interface LocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToPodGallery(galleryEntity: GalleryPodEntity)

    @Query("update favorite_pod set position=position+1")
    fun incrementGalleryPositions()

    @Query("SELECT * FROM favorite_pod ORDER BY position ASC")
    fun getAllFromGallery(): List<GalleryPodEntity>

    @Query("SELECT * FROM favorite_pod WHERE date == :selectedDate")
    fun getPodByDate(selectedDate: String): GalleryPodEntity

    @Delete
    fun deletePod(galleryEntity: GalleryPodEntity)

    @Query("update favorite_pod set position=position-1 where position>(select position from favorite_pod where date=:podDate)")
    fun updateItemPositionsInGalleryWhenDeletingPicture(podDate: String)

    @Query("DELETE FROM favorite_pod WHERE date = :podDate")
    fun deletePodByDate(podDate: String)

    @Query("update favorite_pod set position=position-1 where position in (:posFrom + 1, :posTo)")
    fun adjustPositionsIfMovedDown(posFrom: Int, posTo: Int)

    @Query("update favorite_pod set position=position+1 where position in (:posTo, :posFrom - 1)")
    fun adjustPositionsIfMovedUp(posFrom: Int, posTo: Int)

    @Query("update favorite_pod set position=:posTo where date=:date")
    fun updatePositionOfMovedItem(posTo: Int, date: String)
}