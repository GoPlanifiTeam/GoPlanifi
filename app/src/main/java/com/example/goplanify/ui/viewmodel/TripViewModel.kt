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

    // New state for linked trips
    private val _currentUser = MutableStateFlow<User?>(null)

    init {
        fetchTrips()
    }

    fun getObjectUserTrips(user: User) {
        viewModelScope.launch {
            _currentUser.value = user
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

            // If the user is set, refresh their trips
            _currentUser.value?.let { user ->
                getObjectUserTrips(user)
            }
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

    // New method to delete trip linked to a reservation
    fun deleteTripByReservationId(reservationId: String) {
        viewModelScope.launch {
            val tripToDelete = _trips.value.find { it.linkedReservationId == reservationId }

            if (tripToDelete != null) {
                Log.d("TripViewModel", "Deleting trip ${tripToDelete.id} linked to reservation $reservationId")
                tripRepository.deleteTrip(tripToDelete.id)

                // Refresh the trips list
                fetchTrips()
                _currentUser.value?.let { user ->
                    getObjectUserTrips(user)
                }
            } else {
                Log.d("TripViewModel", "No trip found linked to reservation $reservationId")
            }
        }
    }

    // Find trip linked to a reservation
    fun findTripByReservationId(reservationId: String): Trip? {
        return _trips.value.find { it.linkedReservationId == reservationId }
    }
}