package com.example.goplanify.domain.model

data class ItineraryItem(
    val trip: String,
    val id: String,
    val name: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val images: List<ItineraryImage>? = null
)
