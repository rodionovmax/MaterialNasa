package com.rodionovmax.materialnasa.data.local

import androidx.room.*
import com.rodionovmax.materialnasa.data.Result

@Dao
interface RoverGalleryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoverPhotos(roverPhotos: List<RoverPhotoEntity>)

    @Query("select * from rover_photos where id = :adapterPosition+1 limit 1")
    fun getRoverPhoto(adapterPosition: Int): RoverPhotoEntity

    @Query("delete from rover_photos")
    fun deleteAll()

}