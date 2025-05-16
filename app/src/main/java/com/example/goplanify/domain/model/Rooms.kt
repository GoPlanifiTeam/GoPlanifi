package com.example.goplanify.domain.model

data class Rooms(
    val id: String,
    val roomType: String,
    val price: Float,
    val images: List<String>
)
