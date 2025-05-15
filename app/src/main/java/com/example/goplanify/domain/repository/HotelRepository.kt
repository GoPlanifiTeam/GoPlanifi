package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.utils.Resource

interface HotelRepository {    suspend fun getHotelAvailability(
        destination: String,
        checkIn: String,
        checkOut: String,
        guests: Int,
        groupId: String = "G02"
    ): Resource<List<Hotel>>
    
    suspend fun getHotels(groupId: String): Resource<List<Hotel>>
    
    suspend fun reserveRoom(
        groupId: String,
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): Resource<String> // Returns reservation ID
    
    suspend fun cancelReservation(
        groupId: String,
        hotelId: String,
        roomId: String,
        reservationId: String
    ): Resource<Boolean>
}
