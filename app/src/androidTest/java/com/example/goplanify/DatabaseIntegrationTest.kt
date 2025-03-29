package com.example.goplanify

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.goplanify.data.local.dao.*
import com.example.goplanify.data.local.entity.*
import com.example.goplanify.data.local.AppDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var tripDao: TripDao
    private lateinit var itineraryDao: ItineraryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()
        tripDao = database.tripDao()
        itineraryDao = database.itineraryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUserWithTripAndItineraries() = runBlocking {
        val user = UserEntity(
            userId = "user456",
            email = "demo@example.com",
            password = "password123",
            firstName = "Demo",
            lastName = "Tester",
            imageURL = ""
        )
        userDao.insertUser(user)

        val trip = TripEntity(
            id = "trip456",
            userId = user.userId,
            destination = "Tokyo",
            startDate = Date(),
            endDate = Date(),
            durationDays = 2,
            itinerariesJson = "[]",
            imagesJson = null,
            recommendationsJson = null,
            imageURL = "https://example.com/image.jpg"
        )
        tripDao.insertTrip(trip)

        val itinerary = ItineraryItemEntity(
            id = "it456",
            tripId = trip.id,
            name = "Visit Shibuya",
            location = "Shibuya Crossing",
            startDate = Date(),
            endDate = Date(),
            order = 0
        )
        itineraryDao.insertItineraryItem(itinerary)

        val loadedUser = userDao.getUserById("user456")
        val userTrips = tripDao.getTripsByUser("user456")
        val tripItineraries = itineraryDao.getItineraryItemsByTripId("trip456")

        assertNotNull(loadedUser)
        assertEquals(1, userTrips.size)
        assertEquals("Tokyo", userTrips[0].destination)
        assertEquals(1, tripItineraries.size)
        assertEquals("Visit Shibuya", tripItineraries[0].name)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun cannotInsertDuplicateUserId() = runBlocking {
        val user = UserEntity(
            userId = "duplicate123",
            email = "dup@example.com",
            password = "pass123",
            firstName = "Dup",
            lastName = "User",
            imageURL = ""
        )
        userDao.insertUser(user)
        userDao.insertUser(user)
    }

    @Test
    fun deleteTripKeepsItineraries() = runBlocking {
        val tripId = "tripDel1"
        val trip = TripEntity(
            id = tripId,
            userId = "user1",
            destination = "Rome",
            startDate = Date(),
            endDate = Date(),
            durationDays = 2,
            itinerariesJson = "[]",
            imagesJson = null,
            recommendationsJson = null,
            imageURL = "https://example.com/image.jpg"
        )
        tripDao.insertTrip(trip)

        val itinerary = ItineraryItemEntity(
            id = "iti1",
            tripId = tripId,
            name = "Colosseum",
            location = "Rome",
            startDate = Date(),
            endDate = Date(),
            order = 0
        )
        itineraryDao.insertItineraryItem(itinerary)

        tripDao.deleteTripById(tripId)

        val deletedTrip = tripDao.getTripById(tripId)
        val orphanedItineraries = itineraryDao.getItineraryItemsByTripId(tripId)

        assertNull(deletedTrip)
        assertTrue(orphanedItineraries.isNotEmpty())
    }

}
