package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Trip

interface TripRepository {
    suspend fun addTrip(trip: Trip): Result<Trip>
    suspend fun updateTrip(trip: Trip): Result<Trip>
    suspend fun getTripById(tripId: String): Result<Trip>
    suspend fun getTripsByUser(userId: String): List<Trip>
    suspend fun deleteTrip(tripId: String): Result<Boolean>

}
