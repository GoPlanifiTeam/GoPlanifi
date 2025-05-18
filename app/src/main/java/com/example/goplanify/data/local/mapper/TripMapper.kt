package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.entity.TripEntity
import com.example.goplanify.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

private val gson = Gson()
private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

// De dominio a entidad
fun Trip.toEntity(): TripEntity =
    TripEntity(
        id = id,
        userId = user?.userId ?: "",
        destination = destination,
        startDate = dateFormat.parse(startDate) ?: Date(),
        endDate = dateFormat.parse(endDate) ?: Date(),
        durationDays = calculateDays(startDate, endDate),
        itinerariesJson = gson.toJson(itineraries),
        imagesJson = gson.toJson(images),
        recommendationsJson = gson.toJson(aiRecommendations),
        imageURL = imageURL,
        linkedReservationId = linkedReservationId  // Added linkedReservationId
    )

// De entidad a dominio
fun TripEntity.toDomain(): Trip =
    Trip(
        id = id,
        user = null, // puedes cargarlo desde repositorio si lo necesitas
        destination = destination,
        startDate = dateFormat.format(startDate),
        endDate = dateFormat.format(endDate),
        itineraries = gson.fromJson(itinerariesJson, object : com.google.gson.reflect.TypeToken<List<ItineraryItem>>() {}.type),
        images = gson.fromJson(imagesJson, object : com.google.gson.reflect.TypeToken<List<Image>>() {}.type),
        aiRecommendations = gson.fromJson(recommendationsJson, object : com.google.gson.reflect.TypeToken<List<AIRecommendations>>() {}.type),
        imageURL = imageURL,
        map = null,
        linkedReservationId = linkedReservationId  // Added linkedReservationId
    )

fun TripEntity.toDomain(user: User?, itineraries: List<ItineraryItem>): Trip =
    Trip(
        id = id,
        user = user,
        destination = destination,
        startDate = dateFormat.format(startDate),
        endDate = dateFormat.format(endDate),
        itineraries = itineraries,
        images = gson.fromJson(imagesJson, object : TypeToken<List<Image>>() {}.type),
        aiRecommendations = gson.fromJson(recommendationsJson, object : TypeToken<List<AIRecommendations>>() {}.type),
        imageURL = imageURL,
        map = null,
        linkedReservationId = linkedReservationId  // Added linkedReservationId
    )


private fun calculateDays(start: String, end: String): Int {
    return try {
        val startDate = dateFormat.parse(start)
        val endDate = dateFormat.parse(end)
        val diff = endDate.time - startDate.time
        (diff / (1000 * 60 * 60 * 24)).toInt()
    } catch (e: Exception) {
        0
    }
}