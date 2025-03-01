package com.example.travelplanner.domain

data class AIRecommendations(
    val trip: Trip,
    val recommendations: List<String>,
)
