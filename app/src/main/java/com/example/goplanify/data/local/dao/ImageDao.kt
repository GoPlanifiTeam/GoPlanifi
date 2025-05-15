
package com.example.goplanify.data.local.dao
import androidx.room.*
import com.example.goplanify.domain.model.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<Image>)

    @Update
    suspend fun updateImage(image: Image)

    @Delete
    suspend fun deleteImage(image: Image)

    @Query("DELETE FROM images WHERE id = :imageId")
    suspend fun deleteImageById(imageId: String)

    @Query("DELETE FROM images WHERE tripId = :tripId")
    suspend fun deleteAllImagesForTrip(tripId: String)

    @Query("SELECT * FROM images WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getImagesForTrip(tripId: String): Flow<List<Image>>

    @Query("SELECT * FROM images WHERE id = :imageId")
    suspend fun getImageById(imageId: String): Image?
}