package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.User
import com.example.travelplanner.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> get() = _trips

    fun fetchTripsUser(user: User) {
        _trips.value = tripRepository.getTripsByUser(user)
    }

    // Función para cargar los viajes desde el repositorio
    fun fetchTrips() {
        viewModelScope.launch {
            // Aquí obtienes los viajes desde el repositorio y los asignas al StateFlow
            _trips.value = tripRepository.getTrips()
        }
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
