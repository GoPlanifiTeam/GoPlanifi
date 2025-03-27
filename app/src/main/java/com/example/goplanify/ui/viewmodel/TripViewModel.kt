package com.example.goplanify.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
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

    private val _userTrips = MutableStateFlow<List<Trip>>(emptyList())
    val userTrips: StateFlow<List<Trip>> = _userTrips

    fun updateTrip(updatedTrip: Trip) {
        val result = tripRepository.updateTrip(updatedTrip)
        if (result.isSuccess) {
            Log.d("TripViewModel", "Trip updated successfully")
        } else {
            Log.e("TripViewModel", "Failed to update trip: ${result.exceptionOrNull()?.message}")
        }
    }

    init {
           fetchTrips()
    }

    fun fetchTrips() {
        _trips.value = tripRepository.getTrips()
    }

    fun addTrip(trip: Trip, context: Context) {
        val result = tripRepository.addTrip(trip)
        Log.d("TripViewModel", "Add trip result: $result")
        fetchTrips() // Esto es correcto, refresca todos los trips
    }


    fun getUserTrips(user: User): List<Trip> {
        val filtered = tripRepository.getTripsByUser(user)
        _userTrips.value = filtered
        return filtered
    }


    fun getObjectUserTrips(user: User){
        // This should return trips only for the specified user
        _userTrips.value = tripRepository.getTripsByUser(user)
    }

    fun deleteTrip(tripId: String) {
        tripRepository.deleteTrip(tripId)
    }
}