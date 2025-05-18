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
                val admin = UserEntity(
                    userId = "admin",
                    email = "admin@admin.com", // NOT MEANT TO ENTER WITH THIS ONE!
                    password = "adminadmin",
                    firstName = "Admin",
                    lastName = "Admin",
                    username = "admin",
                    birthDate = null,
                    address = null,
                    country = null,
                    phoneNumber = null,
                    acceptEmails = false,
                    imageURL = null
                )
                userDao.insertUser(admin)

                val trips = listOf(
                    TripEntity(
                        id = "1",
                        userId = admin.userId,
                        destination = "Barcelona",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Visit Sagrada Familia and Park Güell", "location" to "Sagrada Familia, Park Güell", "startDate" to "", "endDate" to "", "trip" to "1"),
                            mapOf("id" to "2", "name" to "Explore Gothic Quarter and La Rambla", "location" to "Gothic Quarter, La Rambla", "startDate" to "", "endDate" to "", "trip" to "1"),
                            mapOf("id" to "3", "name" to "Visit Casa Batlló and Casa Milà", "location" to "Casa Batlló, Casa Milà", "startDate" to "", "endDate" to "", "trip" to "1"),
                            mapOf("id" to "4", "name" to "Relax at Barceloneta Beach and Olympic Port", "location" to "Barceloneta Beach, Olympic Port", "startDate" to "", "endDate" to "", "trip" to "1")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://images.unsplash.com/photo-1583422409516-2895a77efded"
                    ),
                    TripEntity(
                        id = "2",
                        userId = admin.userId,
                        destination = "Paris",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Explore the Eiffel Tower and Champ de Mars", "location" to "Eiffel Tower, Champ de Mars", "startDate" to "", "endDate" to "", "trip" to "2"),
                            mapOf("id" to "2", "name" to "Visit the Louvre Museum and Tuileries Garden", "location" to "Louvre Museum, Tuileries Garden", "startDate" to "", "endDate" to "", "trip" to "2"),
                            mapOf("id" to "3", "name" to "Walk along the Champs-Élysées and visit the Arc de Triomphe", "location" to "Champs-Élysées, Arc de Triomphe", "startDate" to "", "endDate" to "", "trip" to "2"),
                            mapOf("id" to "4", "name" to "Explore Montmartre and visit Sacré-Cœur Basilica", "location" to "Montmartre, Sacré-Cœur Basilica", "startDate" to "", "endDate" to "", "trip" to "2")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34"
                    ),
                    TripEntity(
                        id = "3",
                        userId = admin.userId,
                        destination = "London",
                        startDate = now,
                        endDate = now,
                        durationDays = 1,
                        itinerariesJson = gson.toJson(listOf(
                            mapOf("id" to "1", "name" to "Visit the British Museum and National Gallery", "location" to "British Museum, National Gallery", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "2", "name" to "Explore the Tower of London and Tower Bridge", "location" to "Tower of London, Tower Bridge", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "3", "name" to "See Buckingham Palace and St. James's Park", "location" to "Buckingham Palace, St. James's Park", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "4", "name" to "Ride the London Eye and visit Westminster Abbey", "location" to "London Eye, Westminster Abbey", "startDate" to "", "endDate" to "", "trip" to "3"),
                            mapOf("id" to "5", "name" to "Shop at Covent Garden and explore Soho", "location" to "Covent Garden, Soho", "startDate" to "", "endDate" to "", "trip" to "3")
                        )),
                        imagesJson = "[]",
                        recommendationsJson = "[]",
                        imageURL = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad"
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
