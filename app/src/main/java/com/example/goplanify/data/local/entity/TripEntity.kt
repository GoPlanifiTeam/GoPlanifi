package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val destination: String,
    val startDate: Date,
    val endDate: Date,
    val description: String,
    val imageUrl: String? = null
)
