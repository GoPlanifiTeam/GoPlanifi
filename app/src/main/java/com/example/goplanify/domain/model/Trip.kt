package com.example.goplanify.domain.model

import java.util.Date

data class Trip(
    val id: Long = 0,
    val title: String,
    val destination: String,
    val startDate: Date,
    val endDate: Date,
    val description: String,
    val imageUrl: String? = null
)
