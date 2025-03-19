package com.example.travelplanner.domain.model

data class ItineraryItem(
    val trip: Trip,
    val id: String,
    val name: String,
    val location: String,
    val startDate: String,
    val endDate: String,
)
