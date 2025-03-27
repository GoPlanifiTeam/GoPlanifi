package com.example.goplanify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryItemDao {
    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId")
    fun getItineraryItemsForTrip(tripId: Long): Flow<List<ItineraryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryItem(itineraryItem: ItineraryItemEntity): Long

    @Update
    suspend fun updateItineraryItem(itineraryItem: ItineraryItemEntity)

    @Delete
    suspend fun deleteItineraryItem(itineraryItem: ItineraryItemEntity)
}
