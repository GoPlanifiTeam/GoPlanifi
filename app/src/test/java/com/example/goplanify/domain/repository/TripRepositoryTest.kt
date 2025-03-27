package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.model.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TripRepositoryTest {
    private lateinit var tripRepository: TripRepository
    private lateinit var testUser: User
    private lateinit var tomorrowDate: String
    private lateinit var dayAfterTomorrowDate: String
    
    @Before
    fun setUp() {
        tripRepository = TripRepository()
        testUser = User(
            userId = "test123",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            trips = emptyList(),
            imageURL = null
        )
        
        // Set up future dates for testing
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val tomorrow = LocalDate.now().plusDays(1)
        val dayAfterTomorrow = LocalDate.now().plusDays(2)
        tomorrowDate = tomorrow.format(formatter)
        dayAfterTomorrowDate = dayAfterTomorrow.format(formatter)
    }
    
    @Test
    fun `addTrip with valid data should succeed`() {
        // Arrange
        val trip = Trip(
            id = "trip1",
            destination = "Paris",
            user = testUser,
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        // Act
        val result = tripRepository.addTrip(trip)
        
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(trip, result.getOrNull())
        
        // Verify the trip was added
        val userTrips = tripRepository.getTripsByUser(testUser)
        assertEquals(1, userTrips.size)
        assertEquals(trip, userTrips[0])
    }
    
    @Test
    fun `addTrip with empty destination should fail`() {
        // Arrange
        val trip = Trip(
            id = "trip1",
            destination = "",
            user = testUser,
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        // Act
        val result = tripRepository.addTrip(trip)
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Destination cannot be empty", exception?.message)
    }
    
    @Test
    fun `addTrip with past startDate should fail`() {
        // Arrange
        val pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val trip = Trip(
            id = "trip1",
            destination = "Paris",
            user = testUser,
            startDate = pastDate,
            endDate = tomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        // Act
        val result = tripRepository.addTrip(trip)
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Start date must be in the future", exception?.message)
    }
    
    @Test
    fun `addTrip with endDate before startDate should fail`() {
        // Arrange
        val trip = Trip(
            id = "trip1",
            destination = "Paris",
            user = testUser,
            startDate = dayAfterTomorrowDate,
            endDate = tomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        // Act
        val result = tripRepository.addTrip(trip)
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("End date must be after start date", exception?.message)
    }
    
    @Test
    fun `getTripsByUser should return trips for that user`() {
        // Arrange
        val trip1 = Trip(
            id = "trip1",
            destination = "Paris",
            user = testUser,
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        val trip2 = Trip(
            id = "trip2",
            destination = "London",
            user = testUser,
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        val otherUser = User(
            userId = "other123",
            email = "other@example.com",
            password = "password123",
            firstName = "Other",
            lastName = "User",
            trips = emptyList(),
            imageURL = null
        )
        
        val trip3 = Trip(
            id = "trip3",
            destination = "Berlin",
            user = otherUser,
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        // Add all trips
        tripRepository.addTrip(trip1)
        tripRepository.addTrip(trip2)
        tripRepository.addTrip(trip3)
        
        // Act
        val userTrips = tripRepository.getTripsByUser(testUser)
        
        // Assert
        assertEquals(2, userTrips.size)
        assertTrue(userTrips.contains(trip1))
        assertTrue(userTrips.contains(trip2))
        assertFalse(userTrips.contains(trip3))
    }
    
    @Test
    fun `getTripById should return correct trip`() {
        // Arrange
        val trip = Trip(
            id = "trip1",
            destination = "Paris",
            user = testUser,
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        tripRepository.addTrip(trip)
        
        // Act
        val result = tripRepository.getTripById("trip1")
        
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(trip, result.getOrNull())
    }
    
    @Test
    fun `getTripById with invalid id should fail`() {
        // Act
        val result = tripRepository.getTripById("nonexistent")
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Trip not found", exception?.message)
    }
    
    @Test
    fun `deleteTrip should remove trip`() {
        // Arrange
        val trip = Trip(
            id = "trip1",
            destination = "Paris",
            user = testUser,
            startDate = tomorrowDate,
            endDate = dayAfterTomorrowDate,
            itineraries = emptyList(),
            map = null,
            images = null,
            aiRecommendations = null
        )
        
        tripRepository.addTrip(trip)
        
        // Act
        val result = tripRepository.deleteTrip("trip1")
        
        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
        
        // Verify trip was removed
        val userTrips = tripRepository.getTripsByUser(testUser)
        assertTrue(userTrips.isEmpty())
    }
    
    @Test
    fun `deleteTrip with invalid id should fail`() {
        // Act
        val result = tripRepository.deleteTrip("nonexistent")
        
        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Trip not found", exception?.message)
    }
}
