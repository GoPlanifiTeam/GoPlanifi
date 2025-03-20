package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.User
import com.example.travelplanner.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TripViewModel : ViewModel() {
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> get() = _trips
    private val tripRepository = TripRepository()

    fun fetchTrips(user: User) {
        _trips.value = tripRepository.getTripsByUser(user)
    }

    fun addTrip(trip: Trip) {
        tripRepository.addTrip(trip)
        _trips.value = tripRepository.getTripsByUser(trip.user)
    }

    fun deleteTrip(tripId: String) {
        tripRepository.deleteTrip(tripId)
        _trips.value = tripRepository.getTrips()
    }
}
