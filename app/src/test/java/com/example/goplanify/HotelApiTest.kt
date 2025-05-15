package com.example.goplanify

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.goplanify.data.remote.api.HotelApiService
import com.example.goplanify.data.remote.model.Hotel as DataHotel
import com.example.goplanify.data.remote.model.HotelAvailabilityResponse
import com.example.goplanify.data.remote.model.Room
import com.example.goplanify.data.repository.TestHotelRepositoryImpl
import com.example.goplanify.domain.model.Hotel as DomainHotel
import com.example.goplanify.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class HotelApiTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var hotelApiService: HotelApiService

    private lateinit var hotelRepository: TestHotelRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        hotelRepository = TestHotelRepositoryImpl(hotelApiService)
    }

    @Test
    fun getHotelAvailabilityReturnsSuccess() {
        runBlocking {
            // Given
            val mockResponse = HotelAvailabilityResponse(
                status = "success",
                hotels = listOf(
                    DataHotel(
                        id = "1",
                        name = "Test Hotel",
                        location = "Test Location",
                        stars = 4,
                        price = 150.0,
                        imageUrl = "http://example.com/image.jpg",
                        availability = true,
                        address = "",
                        rooms = emptyList()
                    )
                )
            )

            Mockito.`when`(
                hotelApiService.getHotelAvailability(
                    "Barcelona",
                    "2025-06-01",
                    "2025-06-05",
                    2,
                    "G02"
                )
            ).thenReturn(Response.success(mockResponse))

            // When
            val result = hotelRepository.getHotelAvailability(
                destination = "Barcelona",
                checkIn = "2025-06-01",
                checkOut = "2025-06-05",
                guests = 2,
                groupId = "G02"
            )

            // Then
            assertTrue(result is Resource.Success<*>)
            val successResult = result as Resource.Success<List<DomainHotel>>
            assertEquals(1, successResult.data?.size)
            assertEquals("Test Hotel", successResult.data?.first()?.name)

            // Verify the mock was called with the expected parameters
            Mockito.verify(hotelApiService).getHotelAvailability(
                "Barcelona",
                "2025-06-01",
                "2025-06-05",
                2,
                "G02"
            )
        }
    }

    @Test
    fun getHotelAvailabilityReturnsErrorOnApiFailure() {
        runBlocking {
            // Given
            Mockito.`when`(
                hotelApiService.getHotelAvailability(
                    "Barcelona",
                    "2025-06-01",
                    "2025-06-05",
                    2,
                    "G02"
                )
            ).thenReturn(Response.error(404, "Not found".toResponseBody(null)))

            // When
            val result = hotelRepository.getHotelAvailability(
                destination = "Barcelona",
                checkIn = "2025-06-01",
                checkOut = "2025-06-05",
                guests = 2,
                groupId = "G02"
            )

            // Then
            assertTrue(result is Resource.Error<*>)
            val errorResult = result as Resource.Error<List<DomainHotel>>
            assertTrue(errorResult.message?.contains("404") == true)

            // Verify the mock was called with the expected parameters
            Mockito.verify(hotelApiService).getHotelAvailability(
                "Barcelona",
                "2025-06-01",
                "2025-06-05",
                2,
                "G02"
            )
        }
    }

    @Test
    fun getHotelAvailabilityProperlyMapsRoomData() {
        runBlocking {
            // Given - Create a response with rooms to test room mapping
            val mockResponse = HotelAvailabilityResponse(
                status = "success",
                hotels = listOf(
                    DataHotel(
                        id = "1",
                        name = "Test Hotel",
                        location = "",
                        stars = 4,
                        price = 0.0, // Should be overridden by room price
                        imageUrl = "http://example.com/image.jpg",
                        availability = true,
                        address = "123 Test Street",
                        rooms = listOf(
                            Room(
                                id = "room1",
                                roomType = "Deluxe",
                                price = 199.99,
                                images = listOf("http://example.com/room1.jpg")
                            ),
                            Room(
                                id = "room2",
                                roomType = "Suite",
                                price = 299.99,
                                images = listOf("http://example.com/room2.jpg")
                            )
                        )
                    )
                )
            )

            Mockito.`when`(
                hotelApiService.getHotelAvailability(
                    "Barcelona",
                    "2025-06-01",
                    "2025-06-05",
                    2,
                    "G02"
                )
            ).thenReturn(Response.success(mockResponse))

            // When
            val result = hotelRepository.getHotelAvailability(
                destination = "Barcelona",
                checkIn = "2025-06-01",
                checkOut = "2025-06-05",
                guests = 2,
                groupId = "G02"
            )

            // Then
            assertTrue("Result should be Success but was $result", result is Resource.Success<*>)
            val successResult = result as Resource.Success<List<DomainHotel>>
            val hotel = successResult.data?.firstOrNull()
            assertNotNull("Hotel should not be null", hotel)

            // Check if room data was mapped correctly
            assertEquals("123 Test Street", hotel?.location) // Address mapped to location
            assertEquals(199.99, hotel?.price ?: 0.0, 0.01) // Price taken from first room
            assertEquals(2, hotel?.rooms?.size) // Both rooms mapped
            assertEquals("Deluxe", hotel?.rooms?.get(0)?.type)
            assertEquals(199.99, hotel?.rooms?.get(0)?.price ?: 0.0, 0.01)
            assertEquals("Suite", hotel?.rooms?.get(1)?.type)
            assertEquals(299.99, hotel?.rooms?.get(1)?.price ?: 0.0, 0.01)
        }
    }

    @Test
    fun getHotelAvailabilityWithNullResponseBodyReturnsError() {
        runBlocking {
            // Given - Create a successful response but with null body
            Mockito.`when`(
                hotelApiService.getHotelAvailability(
                    "Barcelona",
                    "2025-06-01",
                    "2025-06-05",
                    2,
                    "G02"
                )
            ).thenReturn(Response.success(null))

            // When
            val result = hotelRepository.getHotelAvailability(
                destination = "Barcelona",
                checkIn = "2025-06-01",
                checkOut = "2025-06-05",
                guests = 2,
                groupId = "G02"
            )

            // Then
            assertTrue("Result should be Error but was $result", result is Resource.Error<*>)
            val errorResult = result as Resource.Error<List<DomainHotel>>
            assertEquals("Response body is null", errorResult.message)
        }
    }

    @Test
    fun getHotelAvailabilityWithEmptyHotelListReturnsEmptyList() {
        runBlocking {
            // Given - Create a response with empty hotels list
            val mockResponse = HotelAvailabilityResponse(
                status = "success",
                hotels = emptyList()
            )

            Mockito.`when`(
                hotelApiService.getHotelAvailability(
                    "Barcelona",
                    "2025-06-01",
                    "2025-06-05",
                    2,
                    "G02"
                )
            ).thenReturn(Response.success(mockResponse))

            // When
            val result = hotelRepository.getHotelAvailability(
                destination = "Barcelona",
                checkIn = "2025-06-01",
                checkOut = "2025-06-05",
                guests = 2,
                groupId = "G02"
            )

            // Then
            assertTrue("Result should be Success but was $result", result is Resource.Success<*>)
            val successResult = result as Resource.Success<List<DomainHotel>>
            assertEquals(0, successResult.data?.size)
        }
    }

    @Test
    fun getHotelAvailabilityWithNetworkExceptionReturnsError() {
        runBlocking {
            // Given - Simulate a network error
            Mockito.`when`(
                hotelApiService.getHotelAvailability(
                    "Barcelona",
                    "2025-06-01",
                    "2025-06-05",
                    2,
                    "G02"
                )
            ).thenAnswer { throw IOException("Network unavailable") }

            // When
            val result = hotelRepository.getHotelAvailability(
                destination = "Barcelona",
                checkIn = "2025-06-01",
                checkOut = "2025-06-05",
                guests = 2,
                groupId = "G02"
            )

            // Then
            assertTrue("Result should be Error but was $result", result is Resource.Error<*>)
            val errorResult = result as Resource.Error<List<DomainHotel>>
            assertTrue(errorResult.message?.contains("Network error") == true)
        }
    }
}