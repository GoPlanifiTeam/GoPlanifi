package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripRepositoryImpl : TripRepository {
    private val trips = mutableListOf<Trip>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun addTrip(trip: Trip): Boolean {
        if (!validateTrip(trip)) {
            return false
        }
        trips.add(trip)
        return true
    }

    override fun editTrip(updatedTrip: Trip): Boolean {
        if (!validateTrip(updatedTrip)) {
            return false
        }

        val index = trips.indexOfFirst { it.id == updatedTrip.id }
        if (index != -1) {
            trips[index] = updatedTrip
            return true
        }
        return false
    }

    override fun deleteTrip(tripId: String): Boolean {
        val initialSize = trips.size
        trips.removeAll { it.id == tripId }
        return trips.size < initialSize
    }

    override fun getTrips(): List<Trip> {
        return trips.toList()
    }

    private fun validateTrip(trip: Trip): Boolean {
        // Check for required fields
        if (trip.id.isBlank() || trip.userId.isBlank() ||
            trip.destination.isBlank() || trip.startDate.isBlank() ||
            trip.endDate.isBlank()) {
            return false
        }

        try {
            // Parse dates
            val today = Date()
            val startDate = dateFormat.parse(trip.startDate)
            val endDate = dateFormat.parse(trip.endDate)

            // Validate dates
            if (startDate == null || endDate == null || startDate.after(endDate)) {
                return false
            }

            // Check if dates are in the future
            if (startDate.before(today)) {
                return false
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }
}