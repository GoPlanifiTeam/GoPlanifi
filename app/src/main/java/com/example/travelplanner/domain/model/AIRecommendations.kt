package com.example.travelplanner.domain.model

import com.example.travelplanner.domain.repository.TripRepository

data class AIRecommendations(
    val trip: TripRepository,
    val recommendations: List<String>,
)
