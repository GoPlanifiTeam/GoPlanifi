package com.example.travelplanner.domain.model

data class AIRecommendations(
    val trip: Trip,
    val recommendations: List<String>,
)
