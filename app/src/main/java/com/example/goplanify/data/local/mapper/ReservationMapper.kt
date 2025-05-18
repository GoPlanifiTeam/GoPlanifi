package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.entity.ReservationEntity
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.model.Reservation
import com.example.goplanify.domain.model.Rooms
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun ReservationEntity.toDomain(): Reservation {
    return Reservation(
        id = id,
        hotelId = hotelId,
        roomId = roomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail,
        hotel = Hotel(
            id = hotelId,
            name = hotelName,
            address = hotelAddress,
            rating = 0, // Default value as we don't store it
            imageUrl = hotelImageUrl
        ),
        room = Rooms(
            id = roomId,
            roomType = roomType,
            price = roomPrice,
            images = emptyList() // We don't store images locally
        )
    )
}

fun Reservation.toEntity(totalPrice: Float, nights: Int, tripId: String? = null): ReservationEntity {
    return ReservationEntity(
        id = id,
        hotelId = hotelId,
        roomId = roomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail,
        hotelName = hotel.name,
        hotelAddress = hotel.address,
        hotelImageUrl = hotel.imageUrl,
        roomType = room.roomType,
        roomPrice = room.price,
        totalPrice = totalPrice,
        nights = nights,
        tripId = tripId
    )
}

fun calculateNights(startDate: String, endDate: String): Int {
    val formatter = DateTimeFormatter.ISO_DATE
    val start = LocalDate.parse(startDate, formatter)
    val end = LocalDate.parse(endDate, formatter)
    return ChronoUnit.DAYS.between(start, end).toInt()
}

fun calculateTotalPrice(pricePerNight: Float, nights: Int): Float {
    return pricePerNight * nights
}