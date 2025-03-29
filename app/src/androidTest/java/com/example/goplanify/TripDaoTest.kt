package com.example.goplanify

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.goplanify.data.local.AppDatabase
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.entity.TripEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.util.Date
import org.junit.Test

class TripDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var tripDao: TripDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        tripDao = database.tripDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetTrip() = runBlocking {
        val trip = TripEntity(
            id = "trip123",
            userId = "user123",
            destination = "Paris",
            startDate = Date(),
            endDate = Date(),
            durationDays = 2,
            itinerariesJson = "[]",
            imagesJson = null,
            recommendationsJson = null,
            imageURL = "https://example.com/paris.jpg"
        )
        tripDao.insertTrip(trip)
        val result = tripDao.getTripById("trip123")
        assertEquals("Paris", result?.destination)
    }
}

