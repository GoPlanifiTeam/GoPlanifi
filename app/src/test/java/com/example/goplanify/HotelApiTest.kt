package com.example.goplanify

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.goplanify.data.remote.api.HotelApiService
import com.example.goplanify.data.remote.dto.AvailabilityResponseDto
import com.example.goplanify.data.remote.dto.HotelDto
import com.example.goplanify.data.remote.dto.RoomDto
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
            val mockResponse = AvailabilityResponseDto(
                available_hotels = listOf(
                    HotelDto(
                        id = "1",
                        name = "Test Hotel",
                        address = "Test Location",
                        rating = 4,
                        image_url = "http://example.com/image.jpg",
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
            val mockResponse = AvailabilityResponseDto(
                available_hotels = listOf(
                    HotelDto(
                        id = "1",
                        name = "Test Hotel",
                        address = "123 Test Street",
                        rating = 4,
                        image_url = "http://example.com/image.jpg",
                        rooms = listOf(
                            RoomDto(
                                id = "room1",
                                room_type = "Deluxe",
                                price = 199.99f,
                                images = listOf("http://example.com/room1.jpg")
                            ),
                            RoomDto(
                                id = "room2",
                                room_type = "Suite",
                                price = 299.99f,
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

            // Check if hotel data was mapped correctly
            assertEquals("1", hotel?.id)
            assertEquals("Test Hotel", hotel?.name)
            assertEquals("123 Test Street", hotel?.address)
            assertEquals(4, hotel?.rating)
            assertEquals("http://example.com/image.jpg", hotel?.imageUrl)

            // Check if rooms data was mapped correctly
            assertEquals(2, hotel?.rooms?.size)

            val room1 = hotel?.rooms?.get(0)
            assertEquals("room1", room1?.id)
            assertEquals("Deluxe", room1?.roomType)
            assertEquals(199.99f, room1?.price ?: 0.0f, 0.01f)
            assertEquals(1, room1?.images?.size)
            assertEquals("http://example.com/room1.jpg", room1?.images?.get(0))

            val room2 = hotel?.rooms?.get(1)
            assertEquals("room2", room2?.id)
            assertEquals("Suite", room2?.roomType)
            assertEquals(299.99f, room2?.price ?: 0.0f, 0.01f)
            assertEquals(1, room2?.images?.size)
            assertEquals("http://example.com/room2.jpg", room2?.images?.get(0))
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
            val mockResponse = AvailabilityResponseDto(
                available_hotels = emptyList()
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