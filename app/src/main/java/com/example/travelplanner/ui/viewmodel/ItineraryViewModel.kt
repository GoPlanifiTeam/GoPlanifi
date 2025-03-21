package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.domain.repository.ItineraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItineraryViewModel : ViewModel() {
    private val _selectedItineraries = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val selectedItineraries: StateFlow<List<ItineraryItem>> get() = _selectedItineraries
    private val repository = ItineraryRepository()
    private val _itineraries = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itineraries: StateFlow<List<ItineraryItem>> get() = _itineraries


    // Method to select or unselect an itinerary
    fun toggleItinerarySelection(itinerary: ItineraryItem) {
        _selectedItineraries.value = if (_selectedItineraries.value.contains(itinerary)) {
            // If already selected, unselect it
            _selectedItineraries.value.filter { it.id != itinerary.id }
        } else {
            // If not selected, select it
            _selectedItineraries.value + itinerary
        }
    }

    // Fetch itinerary items using tripId
    fun fetchItineraryItems(tripId: String) {
        _itineraries.value = repository.getItineraryItemsByTripId(tripId)  // Fetch itinerary items by tripId
    }

    // Add a new itinerary item using the repository
    fun addItineraryItem(tripId: String, activityName: String, location: String) {
        // Use the repository method to add a new itinerary item
        val newItem = repository.addItineraryItem(tripId, activityName, location)

        // Update the itineraries state
        _itineraries.value += newItem
    }

    // Method to delete an itinerary item using the repository
    fun deleteItineraryItem(itineraryId: String) {
        val success = repository.deleteItineraryItem(itineraryId)  // Call the repository to delete
        if (success) {
            // If deletion is successful, update the state
            _itineraries.value = _itineraries.value.filter { it.id != itineraryId }
        }
    }

    // Method to update the start and end dates for a selected itinerary
    fun updateItineraryDates(id: String, selectedStartDate: String, selectedEndDate: String) {
        _itineraries.value = _itineraries.value.map {
            if (it.id == id) {
                it.copy(startDate = selectedStartDate, endDate = selectedEndDate)
            } else {
                it
            }
        }
    }
}

