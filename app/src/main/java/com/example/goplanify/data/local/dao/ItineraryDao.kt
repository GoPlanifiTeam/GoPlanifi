package com.example.goplanify.data.local.dao
import androidx.room.*
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import java.util.Date

@Dao
interface ItineraryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryItem(item: ItineraryItemEntity)

    @Update
    suspend fun updateItineraryItem(item: ItineraryItemEntity)

    @Query("UPDATE itinerary_items SET startDate = :startDate, endDate = :endDate WHERE id = :itineraryId")
    suspend fun updateItineraryItemDates(itineraryId: String, startDate: Date, endDate: Date)


    @Query("DELETE FROM itinerary_items WHERE id = :itineraryId")
    suspend fun deleteItineraryItemById(itineraryId: String): Int

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId")
    suspend fun getItineraryItemsByTripId(tripId: String): List<ItineraryItemEntity>
}
