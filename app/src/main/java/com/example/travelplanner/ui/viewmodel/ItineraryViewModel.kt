package com.example.travelplanner.ui.viewmodel
import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.repository.ItineraryRepository
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.domain.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItineraryViewModel : ViewModel() {
    private val repository = ItineraryRepository()
    private val _itineraries = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itineraries: StateFlow<List<ItineraryItem>> get() = _itineraries

    fun fetchItineraryItems(trip: Trip) {
        _itineraries.value = repository.getItineraryItems(trip)
    }

    fun addItineraryItem(tripId: String, activityName: String, location: String) {
        val newItem = ItineraryItem(
            id = (_itineraries.value.size + 1).toString(),
            name = activityName,
            location = location,
            startDate = "2025-01-01",
            endDate = "2025-01-02",
            trip = TODO()
        )
        _itineraries.value = _itineraries.value + newItem
    }

    fun deleteItineraryItem(itineraryId: String) {
        _itineraries.value = _itineraries.value.filter { it.id != itineraryId }
    }
}
