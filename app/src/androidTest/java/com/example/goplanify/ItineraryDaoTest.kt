package com.example.goplanify

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.goplanify.data.local.AppDatabase
import com.example.goplanify.data.local.dao.ItineraryDao
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class ItineraryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var itineraryDao: ItineraryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        itineraryDao = database.itineraryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetItinerary() = runBlocking {
        val item = ItineraryItemEntity(
            id = "item123",
            tripId = "trip123",
            name = "Visit Eiffel Tower",
            location = "Paris",
            startDate = Date(),
            endDate = Date(),
            order = 1
        )
        itineraryDao.insertItineraryItem(item)
        val result = itineraryDao.getItineraryItemsByTripId("trip123")
        assertTrue(result.isNotEmpty())
    }
}
