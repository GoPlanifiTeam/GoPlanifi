package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.ItineraryItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryRepository @Inject constructor() {
    private val itineraries = mutableListOf<ItineraryItem>()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun addItineraryItem(tripId: String, activityName: String, location: String,
                         startDate: String, endDate: String): Result<ItineraryItem> {
        return when {
            tripId.isBlank() ->
                Result.failure(IllegalArgumentException("Trip ID cannot be empty"))
            activityName.isBlank() ->
                Result.failure(IllegalArgumentException("Activity name cannot be empty"))
            location.isBlank() ->
                Result.failure(IllegalArgumentException("Location cannot be empty"))
            startDate.isBlank() ->
                Result.failure(IllegalArgumentException("Start date cannot be empty"))
            endDate.isBlank() ->
                Result.failure(IllegalArgumentException("End date cannot be empty"))
            !isValidDateFormat(startDate) ->
                Result.failure(IllegalArgumentException("Invalid start date format. Use yyyy-MM-dd"))
            !isValidDateFormat(endDate) ->
                Result.failure(IllegalArgumentException("Invalid end date format. Use yyyy-MM-dd"))
            !isStartDateBeforeEndDate(startDate, endDate) ->
                Result.failure(IllegalArgumentException("End date must be after start date"))
            else -> {

                // for now we just add a item for testing
                val newItem = ItineraryItem(
                    id = UUID.randomUUID().toString(),
                    name = activityName,
                    location = location,
                    startDate = startDate,
                    endDate = endDate,
                    trip = tripId
                )
                itineraries.add(newItem)
                Result.success(newItem)
            }
        }
    }

    fun deleteItineraryItem(itineraryId: String): Result<Boolean> {
        val itemToRemove = itineraries.find { it.id == itineraryId }
        return if (itemToRemove != null) {
            itineraries.remove(itemToRemove)
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Itinerary item not found"))
        }
    }

    fun getItineraryItemsByTripId(tripId: String): List<ItineraryItem> {
        return itineraries.filter { it.trip == tripId }
    }

    private fun isValidDateFormat(dateStr: String): Boolean {
        return try {
            LocalDate.parse(dateStr, dateFormatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    private fun isStartDateBeforeEndDate(startDateStr: String, endDateStr: String): Boolean {
        val startDate = LocalDate.parse(startDateStr, dateFormatter)
        val endDate = LocalDate.parse(endDateStr, dateFormatter)
        return startDate.isBefore(endDate) || startDate.isEqual(endDate)
    }
}