package com.example.goplanify.domain.model

data class Room(
    val id: String,
    val type: String,
    val price: Double,
    val imageUrls: List<String>
)
