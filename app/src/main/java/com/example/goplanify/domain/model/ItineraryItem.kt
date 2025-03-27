package com.example.goplanify.domain.model

import java.util.Date

data class ItineraryItem(
    val id: Long = 0,
    val tripId: Long,
    val title: String,
    val description: String,
    val location: String,
    val date: Date,
    val startTime: Date?,
    val endTime: Date?,
    val type: String
)
