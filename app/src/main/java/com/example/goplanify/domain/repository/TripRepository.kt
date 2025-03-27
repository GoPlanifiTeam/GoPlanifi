package com.example.goplanify.domain.repository

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton
import com.example.goplanify.R
import androidx.compose.ui.res.stringResource


@Singleton
class TripRepository @Inject constructor() {
    private val trips = mutableListOf<Trip>()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")



    fun updateTrip(updatedTrip: Trip): Result<Trip> {
        val index = trips.indexOfFirst { it.id == updatedTrip.id }
        return if (index != -1) {
            trips[index] = updatedTrip
            Log.d("TripRepository", "Trip updated: $updatedTrip")
            Result.success(updatedTrip)
        } else {
            Result.failure(IllegalArgumentException("Trip not found"))
        }
    }

    fun addTrip(trip: Trip): Result<Trip> {
        return when {
            trip.destination.isBlank() ->
                Result.failure(IllegalArgumentException("Destination cannot be empty"))
            trip.user?.userId?.isBlank() == true ->
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
                Log.d("Creacion TripRepo", "Se ha creado el trip correctamente $trip")
                return Result.success(trip)
            }
        }
    }

    fun getTripsByUser(user: User): List<Trip> {
        return trips.filter { it.user?.userId == user.userId }
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




    // Function to fetch predefined trips with empty dates and null user
    fun getTrips(): List<Trip> {
        var testUser = User(
            userId = "test123",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            trips = emptyList(),
            imageURL = null
        )
        return listOf(
            // Paris Adventure
            Trip(
                map = null,
                id = "1",
                destination = "Paris",
                user = testUser,
                startDate = "",
                endDate = "",
                itineraries = listOf(
                    ItineraryItem(id = "1", name = "Explore the Eiffel Tower", location = "Eiffel Tower", startDate = "", endDate = "", trip = "1"),
                    ItineraryItem(id = "2", name = "Day trip to Versailles", location = "Versailles", startDate = "", endDate = "", trip = "1"),
                    ItineraryItem(id = "3", name = "Walk along the Champs-Élysées and visit the Arc de Triomphe", location = "Champs-Élysées, Arc de Triomphe", startDate = "", endDate = "", trip = "1"),
                    ItineraryItem(id = "4", name = "Enjoy a Seine river cruise and visit Notre-Dame Cathedral", location = "Seine River, Notre-Dame Cathedral", startDate = "", endDate = "", trip = "1")
                ),
                images = null,
                aiRecommendations = null,
                imageURL = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34"
            ),
            // Tokyo Cultural Experience
            Trip(
                map = null,
                id = "2",
                destination = "Tokyo",
                user = testUser,
                startDate = "",
                endDate = "",
                itineraries = listOf(
                    ItineraryItem(id = "1", name = "Visit the Senso-ji Temple and Meiji Shrine", location = "Senso-ji Temple, Meiji Shrine", startDate = "", endDate = "", trip = "2"),
                    ItineraryItem(id = "2", name = "Explore Akihabara for anime and technology", location = "Akihabara", startDate = "", endDate = "", trip = "2"),
                    ItineraryItem(id = "3", name = "Discover Odaiba, teamLab Borderless and shopping malls", location = "Odaiba, teamLab Borderless", startDate = "", endDate = "", trip = "2"),
                    ItineraryItem(id = "4", name = "Visit Shibuya Crossing and explore Shinjuku Gyoen National Garden", location = "Shibuya, Shinjuku Gyoen", startDate = "", endDate = "", trip = "2")
                ),
                images = null,
                aiRecommendations = null,
                imageURL = "https://t3.ftcdn.net/jpg/04/98/23/10/360_F_498231018_6w6Zt0h2PdU4Muy5Tvph2VeNG67yTuwl.jpg"
            ),
            // New York City Highlights
            Trip(
                map = null,
                id = "3",
                destination = "New York City",
                user = testUser,
                startDate = "",
                endDate = "",
                itineraries = listOf(
                    ItineraryItem(id = "1", name = "Visit Times Square, Central Park, and Broadway", location = "Times Square, Central Park, Broadway", startDate = "", endDate = "", trip = "3"),
                    ItineraryItem(id = "2", name = "Museum tour (Metropolitan Museum of Art, MoMA)", location = "MoMA, Metropolitan Museum of Art", startDate = "", endDate = "", trip = "3"),
                    ItineraryItem(id = "3", name = "Take the ferry to Liberty Island and visit the Statue of Liberty", location = "Liberty Island", startDate = "", endDate = "", trip = "3"),
                    ItineraryItem(id = "4", name = "Walk the High Line and explore Chelsea Market", location = "High Line, Chelsea Market", startDate = "", endDate = "", trip = "3"),
                    ItineraryItem(id = "5", name = "Explore the Brooklyn Bridge and DUMBO area", location = "Brooklyn Bridge, DUMBO", startDate = "", endDate = "", trip = "3")
                ),
                images = null,
                aiRecommendations = null,
                imageURL = "https://media.istockphoto.com/id/931041896/es/foto/skyline-de-manhattan-con-el-edificio-one-world-trade-center-en-el-crepúsculo.jpg?s=612x612&w=0&k=20&c=Y0S7uDXcYCf9GFXTMCqgaFsoyRpgkVZjFbLgjXeHEqw="
            ),
            // Rome Historical Tour
            Trip(
                map = null,
                id = "4",
                destination = "Rome",
                user = testUser,
                startDate = "",
                endDate = "",
                itineraries = listOf(
                    ItineraryItem(id = "1", name = "Explore the Colosseum and Roman Forum", location = "Colosseum, Roman Forum", startDate = "", endDate = "", trip = "4"),
                    ItineraryItem(id = "2", name = "Vatican Museums and St. Peter's Basilica", location = "Vatican Museums, St. Peter's Basilica", startDate = "", endDate = "", trip = "4"),
                    ItineraryItem(id = "3", name = "Wander through Trastevere and visit Santa Maria in Trastevere", location = "Trastevere, Santa Maria in Trastevere", startDate = "", endDate = "", trip = "4"),
                    ItineraryItem(id = "4", name = "Visit the Pantheon and Piazza Navona", location = "Pantheon, Piazza Navona", startDate = "", endDate = "", trip = "4")
                ),
                images = null,
                aiRecommendations = null,
                imageURL = "https://media.istockphoto.com/id/1196016334/es/foto/rome-skyline-italia.jpg?s=612x612&w=0&k=20&c=YrcoZF1Sd4nG5ccOSMRqlXBh0hh_I8O0IjzLPmOEVtQ="
            ),
            // Sydney Beach Escape
            Trip(
                map = null,
                id = "5",
                destination = "Sydney",
                user = testUser,
                startDate = "",
                endDate = "",
                itineraries = listOf(
                    ItineraryItem(id = "1", name = "Bondi Beach surfing and coastal walk", location = "Bondi Beach", startDate = "", endDate = "", trip = "5"),
                    ItineraryItem(id = "2", name = "Sydney Opera House and Harbour Bridge", location = "Sydney Opera House, Harbour Bridge", startDate = "", endDate = "", trip = "5"),
                    ItineraryItem(id = "3", name = "Royal Botanic Garden and Circular Quay walk", location = "Royal Botanic Garden, Circular Quay", startDate = "", endDate = "", trip = "5"),
                    ItineraryItem(id = "4", name = "Explore Manly Beach and take the ferry back", location = "Manly Beach", startDate = "", endDate = "", trip = "5")
                ),
                images = null,
                aiRecommendations = null,
                imageURL = "https://t3.ftcdn.net/jpg/02/70/06/06/360_F_270060686_NoAEWqnGaAHBnBbToh7qtjBphmatwG5A.jpg"
            )
        )
    }
}


