package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "itinerary_items")
data class ItineraryItemEntity(
    val tripId: String, // ← text
    @PrimaryKey val id: String, // ← text
    val name: String, // ← text
    val location: String, // ← text
    val startDate: Date, // ← datetime
    val endDate: Date,   // ← datetime
    val order: Int       // ← integer
)
