package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.ItineraryItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ItineraryRepositoryTest {
    private lateinit var itineraryRepository: ItineraryRepository
    private lateinit var tripId: String
    private lateinit var tomorrowDate: String
    private lateinit var dayAfterTomorrowDate: String
    
    @Before
    fun setUp() {
        itineraryRepository = ItineraryRepository()
        tripId = "trip123"
        
        // Set up future dates for testing
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val tomorrow = LocalDate.now().plusDays(1)
        val dayAfterTomorrow = LocalDate.now().plusDays(2)
        tomorrowDate = tomorrow.format(formatter)
        dayAfterTomorrowDate = dayAfterTomorrow.format(formatter)
    }
    
    @Test
    fun `addItineraryItem with valid data should succeed`() {
        // Act
        val result = itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "Visit Eiffel Tower",
            location = "Paris, France",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        // Assert
        assertTrue(result.isSuccess)
        val itineraryItem = result.getOrNull()
        assertNotNull(itineraryItem)
        assertEquals("Visit Eiffel Tower", itineraryItem?.name)
        assertEquals("Paris, France", itineraryItem?.location)
        assertEquals(tomorrowDate, itineraryItem?.startDate)
        assertEquals(dayAfterTomorrowDate, itineraryItem?.endDate)
        assertEquals(tripId, itineraryItem?.trip)
        
        // Verify item was added to repository
        val tripItems = itineraryRepository.getItineraryItemsByTripId(tripId)
        assertEquals(1, tripItems.size)
        assertEquals(itineraryItem, tripItems[0])
    }
    
    @Test
    fun `addItineraryItem with empty tripId should fail`() {
        // Act
        val result = itineraryRepository.addItineraryItem(
            tripId = "",
            activityName = "Visit Eiffel Tower",
            location = "Paris, France",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Trip ID cannot be empty", exception?.message)
    }
    
    @Test
    fun `addItineraryItem with empty activityName should fail`() {
        // Act
        val result = itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "",
            location = "Paris, France",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Activity name cannot be empty", exception?.message)
    }
    
    @Test
    fun `addItineraryItem with empty location should fail`() {
        // Act
        val result = itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "Visit Eiffel Tower",
            location = "",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Location cannot be empty", exception?.message)
    }
    
    @Test
    fun `addItineraryItem with invalid date format should fail`() {
        // Act
        val result = itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "Visit Eiffel Tower",
            location = "Paris, France",
            startDate = "01/01/2025", // Wrong format, should be yyyy-MM-dd
            endDate = dayAfterTomorrowDate
        )
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Invalid start date format. Use yyyy-MM-dd", exception?.message)
    }
    
    @Test
    fun `addItineraryItem with endDate before startDate should fail`() {
        // Act
        val result = itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "Visit Eiffel Tower",
            location = "Paris, France",
            startDate = dayAfterTomorrowDate,
            endDate = tomorrowDate
        )
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("End date must be after start date", exception?.message)
    }
    
    @Test
    fun `getItineraryItemsByTripId should return correct items`() {
        // Arrange
        itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "Visit Eiffel Tower",
            location = "Paris, France",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "Visit Louvre",
            location = "Paris, France",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        itineraryRepository.addItineraryItem(
            tripId = "otherTrip",
            activityName = "Visit Big Ben",
            location = "London, UK",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        // Act
        val tripItems = itineraryRepository.getItineraryItemsByTripId(tripId)
        
        // Assert
        assertEquals(2, tripItems.size)
        assertEquals("Visit Eiffel Tower", tripItems[0].name)
        assertEquals("Visit Louvre", tripItems[1].name)
    }
    
    @Test
    fun `deleteItineraryItem should remove item`() {
        // Arrange
        val result = itineraryRepository.addItineraryItem(
            tripId = tripId,
            activityName = "Visit Eiffel Tower",
            location = "Paris, France",
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate
        )
        
        val itineraryItem = result.getOrNull()
        assertNotNull(itineraryItem)
        
        // Act
        val deleteResult = itineraryRepository.deleteItineraryItem(itineraryItem!!.id)
        
        // Assert
        assertTrue(deleteResult.isSuccess)
        assertEquals(true, deleteResult.getOrNull())
        
        // Verify item was removed
        val tripItems = itineraryRepository.getItineraryItemsByTripId(tripId)
        assertTrue(tripItems.isEmpty())
    }
    
    @Test
    fun `deleteItineraryItem with invalid id should fail`() {
        // Act
        val result = itineraryRepository.deleteItineraryItem("nonexistent")
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Itinerary item not found", exception?.message)
    }
}
