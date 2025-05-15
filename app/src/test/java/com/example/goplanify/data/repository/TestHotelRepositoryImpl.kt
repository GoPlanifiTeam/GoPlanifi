package com.example.goplanify.data.repository

import com.example.goplanify.data.remote.api.HotelApiService
import com.example.goplanify.data.remote.mapper.toDomain
import com.example.goplanify.data.remote.model.ReserveRequest
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.repository.HotelRepository
import com.example.goplanify.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is a test-specific implementation of the HotelRepository interface.
 * 
 * We use this class instead of the real HotelRepositoryImpl during unit tests because
 * it allows us to isolate tests from Android-specific dependencies (like Log class)
 */
@Singleton
class TestHotelRepositoryImpl @Inject constructor(
    private val apiService: HotelApiService
) : HotelRepository {
    
    override suspend fun getHotelAvailability(
        destination: String,
        checkIn: String,
        checkOut: String,
        guests: Int,
        groupId: String
    ): Resource<List<Hotel>> {
        return try {
            val response = apiService.getHotelAvailability(destination, checkIn, checkOut, guests, groupId)
            if (response.isSuccessful) {
                val hotelResponse = response.body()
                if (hotelResponse != null) {
                    Resource.Success(hotelResponse.hotels.map { it.toDomain() })
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP error: ${e.message}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.message}")
        }
    }
    
    override suspend fun getHotels(groupId: String): Resource<List<Hotel>> {
        TODO("Not implemented for test")
    }
    
    override suspend fun reserveRoom(
        groupId: String,
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): Resource<String> {
        TODO("Not implemented for test")
    }
    
    override suspend fun cancelReservation(
        groupId: String,
        hotelId: String,
        roomId: String,
        reservationId: String
    ): Resource<Boolean> {
        TODO("Not implemented for test")
    }
}
