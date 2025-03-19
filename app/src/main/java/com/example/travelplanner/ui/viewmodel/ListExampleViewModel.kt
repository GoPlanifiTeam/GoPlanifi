package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListExampleViewModel : ViewModel() {
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> get() = _trips

    fun addTrip(name: String, destination: String, user: User) {
        val newTrip = Trip(
            id = (_trips.value.size + 1).toString(),
            destination = destination,
            user = user,
            startDate = "2025-01-01",
            endDate = "2025-01-07",
            itineraries = emptyList(),
            images = emptyList(),
            aiRecommendations = emptyList(),
            map = null
        )
        _trips.value += newTrip
    }
    fun deleteTrip(tripId: String) {
        _trips.value = _trips.value.filter { it.id != tripId }
    }
}
