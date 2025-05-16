package com.example.goplanify.data.remote.mapper

import com.example.goplanify.data.remote.dto.HotelDto
import com.example.goplanify.data.remote.dto.RoomDto
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.data.remote.dto.ReservationDto
import com.example.goplanify.data.remote.dto.ReserveRequestDto
import com.example.goplanify.domain.model.Reservation
import com.example.goplanify.domain.model.Rooms
import com.example.goplanify.domain.model.ReserveRequest

fun HotelDto.toDomain(): Hotel = Hotel(
    id = id,
    name = name,
    address = address,
    rating = rating,
    imageUrl = image_url,
    rooms = rooms?.map { it.toDomain() } ?: emptyList()
)

fun RoomDto.toDomain(): Rooms = Rooms(
    id = id,
    roomType = room_type,
    price = price,
    images = images ?: emptyList()
)

fun ReservationDto.toDomain(): Reservation = Reservation(
    id = id,
    hotelId = hotel_id,
    roomId = room_id,
    startDate = start_date,
    endDate = end_date,
    guestName = guest_name,
    guestEmail = guest_email,
    hotel = hotel.toDomain(),
    room = room.toDomain()  // Aquí puede seguir habiendo un problema si Reservation espera Room en lugar de Rooms
)

fun ReserveRequest.toDto(): ReserveRequestDto = ReserveRequestDto(
    hotel_id = hotelId,
    room_id = roomId,
    start_date = startDate,
    end_date = endDate,
    guest_name = guestName,
    guest_email = guestEmail
)