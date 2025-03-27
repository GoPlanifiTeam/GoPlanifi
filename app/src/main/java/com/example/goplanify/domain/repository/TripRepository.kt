package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor() {
    private val trips = mutableListOf<Trip>()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun addTrip(trip: Trip): Result<Trip> {
        return when {
            trip.destination.isBlank() ->
                Result.failure(IllegalArgumentException("Destination cannot be empty"))
            trip.user.userId.isBlank() ->
                Result.failure(IllegalArgumentException("User ID cannot be empty"))
            trip.startDate.isBlank() ->
                Result.failure(IllegalArgumentException("Start date cannot be empty"))
            trip.endDate.isBlank() ->
                Result.failure(IllegalArgumentException("End date cannot be empty"))
            !isValidDateFormat(trip.startDate) ->
                Result.failure(IllegalArgumentException("Invalid start date format. Use yyyy-MM-dd"))
            !isValidDateFormat(trip.endDate) ->
                Result.failure(IllegalArgumentException("Invalid end date format. Use yyyy-MM-dd"))
            !isDateInFuture(trip.startDate) ->
                Result.failure(IllegalArgumentException("Start date must be in the future"))
            !isStartDateBeforeEndDate(trip.startDate, trip.endDate) ->
                Result.failure(IllegalArgumentException("End date must be after start date"))
            else -> {
                trips.add(trip)
                Result.success(trip)
            }
        }
    }

    fun getTripsByUser(user: User): List<Trip> {
        return trips.filter { it.user.userId == user.userId }
    }

    fun deleteTrip(tripId: String): Result<Boolean> {
        val removed = trips.removeIf { it.id == tripId }
        return if (removed) {
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Trip not found"))
        }
    }

    fun getTripById(tripId: String): Result<Trip> {
        val trip = trips.find { it.id == tripId }
        return if (trip != null) {
            Result.success(trip)
        } else {
            Result.failure(IllegalArgumentException("Trip not found"))
        }
    }

    private fun isValidDateFormat(dateStr: String): Boolean {
        return try {
            LocalDate.parse(dateStr, dateFormatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    private fun isDateInFuture(dateStr: String): Boolean {
        val date = LocalDate.parse(dateStr, dateFormatter)
        return date.isAfter(LocalDate.now())
    }

    private fun isStartDateBeforeEndDate(startDateStr: String, endDateStr: String): Boolean {
        val startDate = LocalDate.parse(startDateStr, dateFormatter)
        val endDate = LocalDate.parse(endDateStr, dateFormatter)
        return startDate.isBefore(endDate) || startDate.isEqual(endDate)
    }


    // Function to fetch trips (static list for now)
        fun getTrips(): List<Trip> {
            return listOf(
                // Paris Adventure
                Trip(
                    map = null,
                    id = "1",
                    destination = "Paris",
                    user = User(
                        userId = "1",
                        email = "john.doe@example.com",
                        password = "securePassword123",
                        firstName = "John",
                        lastName = "Doe",
                        trips = emptyList(),
                        imageURL = "https://example.com/profile.png"
                    ),
                    startDate = "2025-06-01",
                    endDate = "2025-06-10",
                    itineraries = listOf(
                        ItineraryItem(id = "1", name = "Explore the Eiffel Tower", location = "Eiffel Tower", startDate = "2025-06-01", endDate = "2025-06-03", trip = "1"),
                        ItineraryItem(id = "2", name = "Day trip to Versailles", location = "Versailles", startDate = "2025-06-04", endDate = "2025-06-05", trip = "1"),
                        ItineraryItem(id = "3", name = "Walk along the Champs-Élysées and visit the Arc de Triomphe", location = "Champs-Élysées, Arc de Triomphe", startDate = "2025-06-06", endDate = "2025-06-07", trip = "1"),
                        ItineraryItem(id = "4", name = "Enjoy a Seine river cruise and visit Notre-Dame Cathedral", location = "Seine River, Notre-Dame Cathedral", startDate = "2025-06-08", endDate = "2025-06-09", trip = "1")
                    ),
                    images = null,
                    aiRecommendations = null
                ),
                // Tokyo Cultural Experience
                Trip(
                    map = null,
                    id = "2",
                    destination = "Tokyo",
                    user = User(
                        userId = "2",
                        email = "jane.doe@example.com",
                        password = "securePassword456",
                        firstName = "Jane",
                        lastName = "Doe",
                        trips = emptyList(),
                        imageURL = "https://example.com/profile2.png"
                    ),
                    startDate = "2025-07-01",
                    endDate = "2025-07-10",
                    itineraries = listOf(
                        ItineraryItem(id = "1", name = "Visit the Senso-ji Temple and Meiji Shrine", location = "Senso-ji Temple, Meiji Shrine", startDate = "2025-07-01", endDate = "2025-07-03", trip = "2"),
                        ItineraryItem(id = "2", name = "Explore Akihabara for anime and technology", location = "Akihabara", startDate = "2025-07-04", endDate = "2025-07-06", trip = "2"),
                        ItineraryItem(id = "3", name = "Discover Odaiba, teamLab Borderless and shopping malls", location = "Odaiba, teamLab Borderless", startDate = "2025-07-07", endDate = "2025-07-08", trip = "2"),
                        ItineraryItem(id = "4", name = "Visit Shibuya Crossing and explore Shinjuku Gyoen National Garden", location = "Shibuya, Shinjuku Gyoen", startDate = "2025-07-09", endDate = "2025-07-10", trip = "2")
                    ),
                    images = null,
                    aiRecommendations = null,
                ),
                // New York City Highlights
                Trip(
                    map = null,
                    id = "3",
                    destination = "New York City",
                    user = User(
                        userId = "3",
                        email = "mike.smith@example.com",
                        password = "securePassword789",
                        firstName = "Mike",
                        lastName = "Smith",
                        trips = emptyList(),
                        imageURL = "https://example.com/profile3.png"
                    ),
                    startDate = "2025-08-01",
                    endDate = "2025-08-07",
                    itineraries = listOf(
                        ItineraryItem(id = "1", name = "Visit Times Square, Central Park, and Broadway", location = "Times Square, Central Park, Broadway", startDate = "2025-08-01", endDate = "2025-08-03", trip = "3"),
                        ItineraryItem(id = "2", name = "Museum tour (Metropolitan Museum of Art, MoMA)", location = "MoMA, Metropolitan Museum of Art", startDate = "2025-08-04", endDate = "2025-08-06", trip = "3"),
                        ItineraryItem(id = "3", name = "Take the ferry to Liberty Island and visit the Statue of Liberty", location = "Liberty Island", startDate = "2025-08-04", endDate = "2025-08-05", trip = "3"),
                        ItineraryItem(id = "4", name = "Walk the High Line and explore Chelsea Market", location = "High Line, Chelsea Market", startDate = "2025-08-06", endDate = "2025-08-07", trip = "3"),
                        ItineraryItem(id = "5", name = "Explore the Brooklyn Bridge and DUMBO area", location = "Brooklyn Bridge, DUMBO", startDate = "2025-08-07", endDate = "2025-08-08", trip = "3")
                    ),
                    images = null,
                    aiRecommendations = null,
                ),
                // Rome Historical Tour
                Trip(
                    map = null,
                    id = "4",
                    destination = "Rome",
                    user = User(
                        userId = "4",
                        email = "lucy.jones@example.com",
                        password = "securePassword012",
                        firstName = "Lucy",
                        lastName = "Jones",
                        trips = emptyList(),
                        imageURL = "https://example.com/profile4.png"
                    ),
                    startDate = "2025-09-01",
                    endDate = "2025-09-08",
                    itineraries = listOf(
                        ItineraryItem(id = "1", name = "Explore the Colosseum and Roman Forum", location = "Colosseum, Roman Forum", startDate = "2025-09-01", endDate = "2025-09-03", trip = "4"),
                        ItineraryItem(id = "2", name = "Vatican Museums and St. Peter’s Basilica", location = "Vatican Museums, St. Peter’s Basilica", startDate = "2025-09-04", endDate = "2025-09-06", trip = "4"),
                        ItineraryItem(id = "3", name = "Wander through Trastevere and visit Santa Maria in Trastevere", location = "Trastevere, Santa Maria in Trastevere", startDate = "2025-09-07", endDate = "2025-09-08", trip = "4"),
                        ItineraryItem(id = "4", name = "Visit the Pantheon and Piazza Navona", location = "Pantheon, Piazza Navona", startDate = "2025-09-03", endDate = "2025-09-04", trip = "4")
                    ),
                    images = null,
                    aiRecommendations = null,
                ),
                // Sydney Beach Escape
                Trip(
                    map = null,
                    id = "5",
                    destination = "Sydney",
                    user = User(
                        userId = "5",
                        email = "sarah.brown@example.com",
                        password = "securePassword345",
                        firstName = "Sarah",
                        lastName = "Brown",
                        trips = emptyList(),
                        imageURL = "https://example.com/profile5.png"
                    ),
                    startDate = "2025-10-01",
                    endDate = "2025-10-07",
                    itineraries = listOf(
                        ItineraryItem(id = "1", name = "Bondi Beach surfing and coastal walk", location = "Bondi Beach", startDate = "2025-10-01", endDate = "2025-10-03", trip = "5"),
                        ItineraryItem(id = "2", name = "Sydney Opera House and Harbour Bridge", location = "Sydney Opera House, Harbour Bridge", startDate = "2025-10-04", endDate = "2025-10-06", trip = "5"),
                        ItineraryItem(id = "3", name = "Royal Botanic Garden and Circular Quay walk", location = "Royal Botanic Garden, Circular Quay", startDate = "2025-10-06", endDate = "2025-10-07", trip = "5"),
                        ItineraryItem(id = "4", name = "Explore Manly Beach and take the ferry back", location = "Manly Beach", startDate = "2025-10-02", endDate = "2025-10-03", trip = "5")
                    ),
                    images = null,
                    aiRecommendations = null,
                )
            )
        }

    }

