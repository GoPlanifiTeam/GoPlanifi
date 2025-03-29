package com.example.goplanify.data.local

import com.example.goplanify.data.local.dao.ItineraryDao
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.dao.UserDao
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import com.example.goplanify.data.local.entity.TripEntity
import com.example.goplanify.data.local.entity.UserEntity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val tripDao: TripDao,
    private val userDao: UserDao,
    private val itineraryDao: ItineraryDao
) {
    private val gson = Gson()
    private val now = Date()

    fun populateIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            if (userDao.getAllUsers().isEmpty() && tripDao.getAllTrips().isEmpty()) {
                val user = UserEntity(
                    userId = "test123",
                    email = "test@example.com", //Default User
                    password = "password123", //Default password
                    firstName = "Test",
                    lastName = "User",
                    imageURL = null
                )
                val admin = UserEntity(
                    userId = "admin",
                    email = "admin@admin.com", //NOT MEANT TO ENTER WITH THIS ONE VITOR!
                    password = "admin",
                    firstName = "Admin",
                    lastName = "Admin",
                    imageURL = null
                )
                userDao.insertUser(admin)
                userDao.insertUser(user)

                val trips = listOf(
                    TripEntity(
                        id = "1",
                        userId = admin.userId,
                        destination = "Paris",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Explore the Eiffel Tower", "location" to "Eiffel Tower", "startDate" to "", "endDate" to "", "trip" to "1"),
                            mapOf("id" to "2", "name" to "Day trip to Versailles", "location" to "Versailles", "startDate" to "", "endDate" to "", "trip" to "1"),
                            mapOf("id" to "3", "name" to "Walk along the Champs-Élysées and visit the Arc de Triomphe", "location" to "Champs-Élysées, Arc de Triomphe", "startDate" to "", "endDate" to "", "trip" to "1"),
                            mapOf("id" to "4", "name" to "Enjoy a Seine river cruise and visit Notre-Dame Cathedral", "location" to "Seine River, Notre-Dame Cathedral", "startDate" to "", "endDate" to "", "trip" to "1")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34"
                    ),
                    TripEntity(
                        id = "2",
                        userId = admin.userId,
                        destination = "Tokyo",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Visit the Senso-ji Temple and Meiji Shrine", "location" to "Senso-ji Temple, Meiji Shrine", "startDate" to "", "endDate" to "", "trip" to "2"),
                            mapOf("id" to "2", "name" to "Explore Akihabara for anime and technology", "location" to "Akihabara", "startDate" to "", "endDate" to "", "trip" to "2"),
                            mapOf("id" to "3", "name" to "Discover Odaiba, teamLab Borderless and shopping malls", "location" to "Odaiba, teamLab Borderless", "startDate" to "", "endDate" to "", "trip" to "2"),
                            mapOf("id" to "4", "name" to "Visit Shibuya Crossing and explore Shinjuku Gyoen National Garden", "location" to "Shibuya, Shinjuku Gyoen", "startDate" to "", "endDate" to "", "trip" to "2")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://t3.ftcdn.net/jpg/04/98/23/10/360_F_498231018_6w6Zt0h2PdU4Muy5Tvph2VeNG67yTuwl.jpg"
                    ),
                    TripEntity(
                        id = "3",
                        userId = admin.userId,
                        destination = "New York City",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Visit Times Square, Central Park, and Broadway", "location" to "Times Square, Central Park, Broadway", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "2", "name" to "Museum tour (Metropolitan Museum of Art, MoMA)", "location" to "MoMA, Metropolitan Museum of Art", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "3", "name" to "Take the ferry to Liberty Island and visit the Statue of Liberty", "location" to "Liberty Island", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "4", "name" to "Walk the High Line and explore Chelsea Market", "location" to "High Line, Chelsea Market", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "5", "name" to "Explore the Brooklyn Bridge and DUMBO area", "location" to "Brooklyn Bridge, DUMBO", "startDate" to "", "endDate" to "", "trip" to "3")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://media.istockphoto.com/id/931041896/es/foto/skyline-de-manhattan-con-el-edificio-one-world-trade-center-en-el-crepúsculo.jpg"
                    ),
                    TripEntity(
                        id = "4",
                        userId = admin.userId,
                        destination = "Rome",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Explore the Colosseum and Roman Forum", "location" to "Colosseum, Roman Forum", "startDate" to "", "endDate" to "", "trip" to "4"),
                            mapOf("id" to "2", "name" to "Vatican Museums and St. Peter's Basilica", "location" to "Vatican Museums, St. Peter's Basilica", "startDate" to "", "endDate" to "", "trip" to "4"),
                            mapOf("id" to "3", "name" to "Wander through Trastevere and visit Santa Maria in Trastevere", "location" to "Trastevere, Santa Maria in Trastevere", "startDate" to "", "endDate" to "", "trip" to "4"),
                            mapOf("id" to "4", "name" to "Visit the Pantheon and Piazza Navona", "location" to "Pantheon, Piazza Navona", "startDate" to "", "endDate" to "", "trip" to "4")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://media.istockphoto.com/id/1196016334/es/foto/rome-skyline-italia.jpg"
                    ),
                    TripEntity(
                        id = "5",
                        userId = admin.userId,
                        destination = "Sydney",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Bondi Beach surfing and coastal walk", "location" to "Bondi Beach", "startDate" to "", "endDate" to "", "trip" to "5"),
                            mapOf("id" to "2", "name" to "Sydney Opera House and Harbour Bridge", "location" to "Sydney Opera House, Harbour Bridge", "startDate" to "", "endDate" to "", "trip" to "5"),
                            mapOf("id" to "3", "name" to "Royal Botanic Garden and Circular Quay walk", "location" to "Royal Botanic Garden, Circular Quay", "startDate" to "", "endDate" to "", "trip" to "5"),
                            mapOf("id" to "4", "name" to "Explore Manly Beach and take the ferry back", "location" to "Manly Beach", "startDate" to "", "endDate" to "", "trip" to "5")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://t3.ftcdn.net/jpg/02/70/06/06/360_F_270060686_NoAEWqnGaAHBnBbToh7qtjBphmatwG5A.jpg"
                    )
                )

                tripDao.insertAllTrips(trips)
                trips.forEach { trip ->
                    val itineraries: List<Map<String, String>> =
                        gson.fromJson(trip.itinerariesJson, object : com.google.gson.reflect.TypeToken<List<Map<String, String>>>() {}.type)

                    itineraries.forEach { item ->
                        itineraryDao.insertItineraryItem(
                            ItineraryItemEntity(
                                id = UUID.randomUUID().toString(),
                                tripId = trip.id,
                                name = item["name"] ?: "",
                                location = item["location"] ?: "",
                                startDate = now,
                                endDate = now,
                                order = 0
                            )
                        )
                    }
                }
            }
        }
    }
}
