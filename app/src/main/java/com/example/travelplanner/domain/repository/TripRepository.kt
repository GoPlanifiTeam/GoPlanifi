package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.User

class TripRepository {
    private val trips = mutableListOf<Trip>()

    fun addTrip(trip: Trip) {
        trips.add(trip)
    }

    fun getTripsByUser(user: User): List<Trip> {
        return trips.filter { it.user == user }
    }

    fun deleteTrip(tripId: String) {
        trips.removeIf { it.id == tripId }
    }
}
