package com.example.goplanify.data.remote.model

import com.google.gson.annotations.SerializedName

// Response for hotel availability endpoint
data class HotelAvailabilityResponse(
    @SerializedName("hotels") val hotels: List<Hotel> = emptyList(),
    @SerializedName("status") val status: String = ""
)

// Response for hotel listing endpoint
data class HotelListResponse(
    @SerializedName("hotels") val hotels: List<Hotel> = emptyList()
)

// Hotel model based on API specification
data class Hotel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("rating") val stars: Int,
    @SerializedName("rooms") val rooms: List<Room> = emptyList(),
    @SerializedName("image_url") val imageUrl: String,
    val location: String = "", // For backward compatibility
    val price: Double = 0.0,   // For backward compatibility
    val availability: Boolean = true // Local field, not from API
)

// Room model based on API specification
data class Room(
    @SerializedName("id") val id: String,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("price") val price: Double,
    @SerializedName("images") val images: List<String> = emptyList()
)

// Request body for room reservation
data class ReserveRequest(
    @SerializedName("hotel_id") val hotelId: String,
    @SerializedName("room_id") val roomId: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("guest_name") val guestName: String,
    @SerializedName("guest_email") val guestEmail: String
)

// Response for reservation endpoint
data class ReservationResponse(
    @SerializedName("reservation_id") val reservationId: String,
    @SerializedName("status") val status: String,
    @SerializedName("hotel") val hotel: Hotel,
    @SerializedName("room") val room: Room,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("guest_name") val guestName: String,
    @SerializedName("guest_email") val guestEmail: String,
    @SerializedName("total_price") val totalPrice: Double
)
