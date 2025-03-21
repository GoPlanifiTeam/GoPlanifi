package com.example.goplanify.domain.model

import com.example.goplanify.domain.repository.TripRepository

data class AIRecommendations(
    val trip: TripRepository,
    val recommendations: List<String>,
)
