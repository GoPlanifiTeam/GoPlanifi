package com.example.travelplanner.domain.model

import com.example.travelplanner.domain.repository.TripRepository

data class Image(
    val trip: Trip,
    val id: Int,
    val imageURL: String,
)
