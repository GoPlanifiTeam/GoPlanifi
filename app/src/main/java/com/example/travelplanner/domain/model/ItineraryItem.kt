package com.example.travelplanner.domain.model

data class ItineraryItem(
    val id: String,
    val tripId: String,
    val name: String,
    val location: String,
    val startDate: String,
    val endDate: String
)