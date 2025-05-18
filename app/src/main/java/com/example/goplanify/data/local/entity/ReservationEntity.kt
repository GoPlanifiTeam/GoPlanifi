package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("tripId")]
)
data class ReservationEntity(
    @PrimaryKey
    val id: String,
    val hotelId: String,
    val roomId: String,
    val startDate: String,
    val endDate: String,
    val guestName: String,
    val guestEmail: String,
    val hotelName: String,
    val hotelAddress: String,
    val hotelImageUrl: String,
    val roomType: String,
    val roomPrice: Float,
    val totalPrice: Float,
    val nights: Int,
    val tripId: String? = null
)