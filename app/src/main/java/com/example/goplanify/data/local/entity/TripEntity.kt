package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val destination: String,
    val startDate: Date, // ← datetime
    val endDate: Date,   // ← datetime
    val durationDays: Int, // ← integer
    val itinerariesJson: String?, // ← text
    val imagesJson: String?,      // ← text
    val recommendationsJson: String?, // ← text
    val imageURL: String // ← text
)
