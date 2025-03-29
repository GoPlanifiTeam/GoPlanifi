package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.ItineraryItem

interface ItineraryRepository {
    suspend fun addItineraryItem(item: ItineraryItem): Result<ItineraryItem>
    suspend fun deleteItineraryItem(itineraryId: String): Result<Boolean>
    suspend fun getItineraryItemsByTripId(tripId: String): List<ItineraryItem>
    suspend fun updateItineraryDates(itineraryId: String, startDate: String, endDate: String)
}
