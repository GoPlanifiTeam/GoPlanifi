package com.example.goplanify.data.remote.mapper

import com.example.goplanify.data.remote.model.Hotel as HotelDto
import com.example.goplanify.data.remote.model.Room as RoomDto
import com.example.goplanify.domain.model.Hotel as HotelDomain
import com.example.goplanify.domain.model.Room as RoomDomain

fun HotelDto.toDomain(): HotelDomain {
    return HotelDomain(
        id = id,
        name = name,
        location = address, // Map address to location
        stars = stars,
        price = if (rooms.isNotEmpty()) rooms.firstOrNull()?.price ?: 0.0 else price, // Use room price if available
        imageUrl = imageUrl,
        availability = availability,
        rooms = rooms.map { it.toDomain() }
    )
}

fun RoomDto.toDomain(): RoomDomain {
    return RoomDomain(
        id = id,
        type = roomType,
        price = price,
        imageUrls = images
    )
}
