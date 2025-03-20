package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.domain.repository.ItineraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItineraryViewModel : ViewModel() {
    private val repository = ItineraryRepository()
    private val _itineraries = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itineraries: StateFlow<List<ItineraryItem>> get() = _itineraries

    // Modify the method to use tripId (String) instead of Trip object
    fun fetchItineraryItems(tripId: String) {
        _itineraries.value = repository.getItineraryItemsByTripId(tripId)  // Fetch itinerary items by tripId
    }

    // Method to add a new itinerary item, using tripId
    fun addItineraryItem(tripId: String, activityName: String, location: String) {
        val newItem = ItineraryItem(
            id = (_itineraries.value.size + 1).toString(),
            name = activityName,
            location = location,
            startDate = "2025-01-01",
            endDate = "2025-01-02",
            trip = tripId
        )
        _itineraries.value = _itineraries.value + newItem
    }

    // Method to delete an itinerary item
    fun deleteItineraryItem(itineraryId: String) {
        _itineraries.value = _itineraries.value.filter { it.id != itineraryId }
    }
}
