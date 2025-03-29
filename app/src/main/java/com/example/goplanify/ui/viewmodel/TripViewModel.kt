package com.example.goplanify.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.model.User
import com.example.goplanify.domain.repository.TripRepository
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
    val trips: StateFlow<List<Trip>> = _trips

    private val _userTrips = MutableStateFlow<List<Trip>>(emptyList())
    val userTrips: StateFlow<List<Trip>> = _userTrips

    init {
        fetchTrips()
    }

    fun getObjectUserTrips(user: User) {
        viewModelScope.launch {
            val trips = tripRepository.getTripsByUser(user.userId)
            Log.d("TripViewModel", "Trips fetched  $trips")
            _userTrips.value = trips
        }
    }

    fun getObjectUserTrips(userId: String) {
        viewModelScope.launch {
            val trips = tripRepository.getTripsByUser(userId)
            Log.d("TripViewModel", "Trips fetched for userId=$userId: $trips")
            _userTrips.value = trips
        }
    }

    fun fetchTrips() {
        viewModelScope.launch {
            _trips.value = tripRepository.getTripsByUser("admin") //AAA
        }
    }

    fun fetchUserTrips(userId: String) {
        viewModelScope.launch {
            _userTrips.value = tripRepository.getTripsByUser(userId)
        }
    }

    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.addTrip(trip)
            fetchTrips()
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.updateTrip(trip)
            fetchTrips()
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            tripRepository.deleteTrip(tripId)
            fetchTrips()
        }
    }
}

