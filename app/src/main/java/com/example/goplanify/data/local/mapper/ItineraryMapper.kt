package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.entity.ItineraryItemEntity
import com.example.goplanify.domain.model.ItineraryItem
import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun ItineraryItem.toEntity(): ItineraryItemEntity =
    ItineraryItemEntity(
        id = id,
        tripId = trip,
        name = name,
        location = location,
        startDate = dateFormat.parse(startDate) ?: Date(),
        endDate = dateFormat.parse(endDate) ?: Date(),
        order = 0 // Puedes modificar según tu lógica
    )

fun ItineraryItemEntity.toDomain(): ItineraryItem =
    ItineraryItem(
        id = id,
        trip = tripId,
        name = name,
        location = location,
        startDate = dateFormat.format(startDate),
        endDate = dateFormat.format(endDate)
    )

