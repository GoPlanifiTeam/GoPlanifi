package com.example.travelplanner.domain.model

data class AIRecommendations(
    val id: String,
    val tripId: String,
    val recommendations: List<String>
)