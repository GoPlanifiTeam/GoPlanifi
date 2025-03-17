// Trip.kt
package com.example.travelplanner.domain.model

data class Trip(
    val id: String,
    val userId: String,
    val mapId: String,
    val destination: String,
    val startDate: String,
    val endDate: String
)