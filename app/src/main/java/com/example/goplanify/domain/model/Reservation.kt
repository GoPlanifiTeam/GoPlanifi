package com.example.goplanify.domain.model

data class Reservation(
    val id: String,
    val hotelId: String,
    val roomId: String,
    val startDate: String,
    val endDate: String,
    val guestName: String,
    val guestEmail: String,
    val hotel: Hotel,
    val room:  Rooms
)
