package com.example.goplanify.domain.model

data class Trip(
    val map: Map?,
    val id: String,
    val user: User?,
    val destination: String,
    val itineraries: List<ItineraryItem> = emptyList(),
    val startDate: String,
    val endDate: String,
    val images: List<Image>?,
    val aiRecommendations: List<AIRecommendations>?,
    val imageURL: String = "https://example.com/default-trip-image.jpg"
)

