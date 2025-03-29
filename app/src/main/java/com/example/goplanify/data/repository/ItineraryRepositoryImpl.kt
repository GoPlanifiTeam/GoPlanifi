package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.ItineraryDao
import com.example.goplanify.data.local.mapper.toDomain
import com.example.goplanify.data.local.mapper.toEntity
import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.repository.ItineraryRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryRepositoryImpl @Inject constructor(
    private val dao: ItineraryDao
) : ItineraryRepository {

    override suspend fun addItineraryItem(item: ItineraryItem): Result<ItineraryItem> {
        return try {
            dao.insertItineraryItem(item.toEntity())
            Log.d("DB-Itinerary", "Itinerary inserted: ${item.id} for trip ${item.trip}")
            Result.success(item)
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error inserting itinerary: ${item.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteItineraryItem(itineraryId: String): Result<Boolean> {
        return try {
            dao.deleteItineraryItemById(itineraryId)
            Log.d("DB-Itinerary", "Itinerary deleted: $itineraryId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error deleting itinerary: $itineraryId", e)
            Result.failure(e)
        }
    }

    override suspend fun getItineraryItemsByTripId(tripId: String): List<ItineraryItem> {
        return try {
            val items = dao.getItineraryItemsByTripId(tripId).map { it.toDomain() }
            Log.d("DB-Itinerary", "Fetched ${items.size} itineraries for tripId=$tripId")
            items
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error fetching itineraries for tripId=$tripId", e)
            emptyList()
        }
    }

    override suspend fun updateItineraryDates(itineraryId: String, startDate: String, endDate: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = formatter.parse(startDate) ?: Date()
        val end = formatter.parse(endDate) ?: Date()
        try {
            dao.updateItineraryItemDates(itineraryId, start, end)
            Log.d("DB-Itinerary", "Updated dates for itinerary $itineraryId → $start to $end")
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error updating dates for itinerary $itineraryId", e)
        }
    }
}
