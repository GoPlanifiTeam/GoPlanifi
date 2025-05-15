package com.example.goplanify.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val location: String,
    val stars: Int,
    val price: Double,
    val imageUrl: String,
    val availability: Boolean,
    val rooms: List<Room> = emptyList()
)
