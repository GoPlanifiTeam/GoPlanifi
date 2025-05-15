package com.example.goplanify.data.local.dao

import androidx.room.*
import com.example.goplanify.data.local.entity.ItineraryImageEntity

@Dao
interface ItineraryImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryImage(image: ItineraryImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryImages(images: List<ItineraryImageEntity>)

    @Update
    suspend fun updateItineraryImage(image: ItineraryImageEntity)

    @Delete
    suspend fun deleteItineraryImage(image: ItineraryImageEntity)

    @Query("DELETE FROM itinerary_images WHERE id = :imageId")
    suspend fun deleteItineraryImageById(imageId: String)

    @Query("DELETE FROM itinerary_images WHERE itineraryId = :itineraryId")
    suspend fun deleteAllImagesForItinerary(itineraryId: String)

    @Query("SELECT * FROM itinerary_images WHERE itineraryId = :itineraryId")
    suspend fun getImagesForItinerary(itineraryId: String): List<ItineraryImageEntity>

    @Query("SELECT * FROM itinerary_images WHERE id = :imageId")
    suspend fun getItineraryImageById(imageId: String): ItineraryImageEntity?
}