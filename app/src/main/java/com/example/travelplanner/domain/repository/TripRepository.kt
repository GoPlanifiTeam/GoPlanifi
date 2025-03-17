package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Trip

interface TripRepository {
    fun addTrip(trip: Trip): Boolean
    fun editTrip(updatedTrip: Trip): Boolean
    fun deleteTrip(tripId: String): Boolean
    fun getTrips(): List<Trip>
}