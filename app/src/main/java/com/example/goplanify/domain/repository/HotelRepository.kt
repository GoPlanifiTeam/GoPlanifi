package com.example.goplanify.domain.repository

import com.example.goplanify.data.local.entity.ReservationEntity
import com.example.goplanify.data.remote.dto.ReservationResponseDto
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.model.Reservation
import com.example.goplanify.utils.Resource

interface HotelRepository {
    suspend fun getHotelAvailability(
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
    ): Resource<ReservationResponseDto>

    suspend fun cancelReservation(reservationId: String): Resource<Reservation>

    suspend fun getReservations(
        groupId: String,
        guestEmail: String? = null
    ): Resource<List<Reservation>>

    // Local methods
    suspend fun saveReservationLocally(reservation: ReservationEntity)
    suspend fun getLocalReservations(email: String): List<ReservationEntity>
    suspend fun deleteLocalReservation(reservationId: String)
    suspend fun assignReservationToTrip(reservationId: String, tripId: String)
    suspend fun removeReservationFromTrip(reservationId: String)
    suspend fun getReservationsForTrip(tripId: String): List<ReservationEntity>
}
