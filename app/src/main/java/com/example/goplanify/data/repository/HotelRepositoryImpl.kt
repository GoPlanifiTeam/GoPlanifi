package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.remote.api.HotelApiService
import com.example.goplanify.data.remote.dto.ReserveRequestDto
import com.example.goplanify.data.remote.mapper.toDomain
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.repository.HotelRepository
import com.example.goplanify.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val apiService: HotelApiService
) : HotelRepository {

    private val TAG = "HotelRepository"

    override suspend fun getHotelAvailability(
        destination: String,
        checkIn: String,
        checkOut: String,
        guests: Int,
        groupId: String
    ): Resource<List<Hotel>> {
        return try {
            val response = apiService.getHotelAvailability(groupId, destination, checkIn, checkOut, guests)
            if (response.isSuccessful) {
                val availabilityResponse = response.body()
                if (availabilityResponse != null) {
                    Log.d(TAG, "Successfully fetched ${availabilityResponse.available_hotels.size} hotels")
                    Resource.Success(availabilityResponse.available_hotels.map { it.toDomain() })
                } else {
                    Log.e(TAG, "Response body is null for hotel availability")
                    Resource.Error("Response body is null")
                }
            } else {
                Log.e(TAG, "Error fetching hotel availability: ${response.code()}: ${response.message()}")
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching hotel availability", e)
            Resource.Error("HTTP error: ${e.message}")
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching hotel availability", e)
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error while fetching hotel availability", e)
            Resource.Error("Unknown error: ${e.message}")
        }
    }

    override suspend fun getHotels(groupId: String): Resource<List<Hotel>> {
        return try {
            val response = apiService.getHotels(groupId)
            if (response.isSuccessful) {
                val hotels = response.body()
                if (hotels != null) {
                    Log.d(TAG, "Successfully fetched ${hotels.size} hotels")
                    Resource.Success(hotels.map { it.toDomain() })
                } else {
                    Log.e(TAG, "Response body is null for hotel list")
                    Resource.Error("Response body is null")
                }
            } else {
                Log.e(TAG, "Error fetching hotels: ${response.code()}: ${response.message()}")
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while fetching hotels", e)
            Resource.Error("HTTP error: ${e.message}")
        } catch (e: IOException) {
            Log.e(TAG, "Network error while fetching hotels", e)
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error while fetching hotels", e)
            Resource.Error("Unknown error: ${e.message}")
        }
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
        return try {
            val request = ReserveRequestDto(
                hotel_id = hotelId,
                room_id = roomId,
                start_date = startDate,
                end_date = endDate,
                guest_name = guestName,
                guest_email = guestEmail
            )

            val response = apiService.reserveRoom(groupId, request)
            if (response.isSuccessful) {
                val reservationResponse = response.body()
                if (reservationResponse != null) {
                    Log.d(TAG, "Successfully created reservation")
                    Resource.Success(reservationResponse.reservation.id)
                } else {
                    Log.e(TAG, "Response body is null for reservation")
                    Resource.Error("Response body is null")
                }
            } else {
                Log.e(TAG, "Error creating reservation: ${response.code()}: ${response.message()}")
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while creating reservation", e)
            Resource.Error("HTTP error: ${e.message}")
        } catch (e: IOException) {
            Log.e(TAG, "Network error while creating reservation", e)
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error while creating reservation", e)
            Resource.Error("Unknown error: ${e.message}")
        }
    }

    override suspend fun cancelReservation(
        groupId: String,
        hotelId: String,
        roomId: String,
        reservationId: String
    ): Resource<Boolean> {
        return try {
            // Since we need reservation details for cancellation according to the API
            val request = ReserveRequestDto(
                hotel_id = hotelId,
                room_id = roomId,
                start_date = "", // Not used for cancellation
                end_date = "",   // Not used for cancellation
                guest_name = "", // Not used for cancellation
                guest_email = ""  // Not used for cancellation
            )

            val response = apiService.cancelReservation(groupId, request)
            if (response.isSuccessful) {
                Log.d(TAG, "Successfully cancelled reservation $reservationId")
                Resource.Success(true)
            } else {
                Log.e(TAG, "Error cancelling reservation: ${response.code()}: ${response.message()}")
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while cancelling reservation", e)
            Resource.Error("HTTP error: ${e.message}")
        } catch (e: IOException) {
            Log.e(TAG, "Network error while cancelling reservation", e)
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error while cancelling reservation", e)
            Resource.Error("Unknown error: ${e.message}")
        }
    }

    // Helper function to handle exceptions
    private fun <T> handleException(e: Exception, action: String): Resource<T> {
        return when (e) {
            is HttpException -> {
                Log.e(TAG, "HTTP error while $action", e)
                Resource.Error("HTTP error: ${e.message}")
            }
            is IOException -> {
                Log.e(TAG, "Network error while $action", e)
                Resource.Error("Network error: ${e.message}")
            }
            else -> {
                Log.e(TAG, "Unknown error while $action", e)
                Resource.Error("Unknown error: ${e.message}")
            }
        }
    }
}