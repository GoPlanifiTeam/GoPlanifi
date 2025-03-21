package com.example.goplanify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.model.User
import com.example.goplanify.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips

    init {
        fetchTrips()
    }

    fun fetchTrips() {
        _trips.value = tripRepository.getTrips()
    }

    fun addTrip(trip: Trip) {
        tripRepository.addTrip(trip)
        fetchTrips()
    }

    fun getUserTrips(user: User): List<Trip> {
        return tripRepository.getTripsByUser(user)
    }

    fun deleteTrip(tripId: String) {
        tripRepository.deleteTrip(tripId)
        fetchTrips()
    }
}