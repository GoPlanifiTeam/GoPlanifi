package com.example.goplanify.data.repository

import android.util.Log
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
            val response = apiService.getHotelAvailability(destination, checkIn, checkOut, guests)
            if (response.isSuccessful) {
                val hotelResponse = response.body()
                if (hotelResponse != null) {
                    Log.d(TAG, "Successfully fetched ${hotelResponse.hotels.size} hotels")
                    Resource.Success(hotelResponse.hotels.map { it.toDomain() })
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
                val hotelsResponse = response.body()
                if (hotelsResponse != null) {
                    Log.d(TAG, "Successfully fetched ${hotelsResponse.hotels.size} hotels")
                    Resource.Success(hotelsResponse.hotels.map { it.toDomain() })
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
            val request = ReserveRequest(
                hotelId = hotelId,
                roomId = roomId,                startDate = startDate,
                endDate = endDate,
                guestName = guestName,
                guestEmail = guestEmail
            )
              val response = apiService.reserveRoom(groupId, request)
            if (response.isSuccessful) {
                val reservationResponse = response.body()
                if (reservationResponse != null) {
                    Log.d(TAG, "Successfully created reservation")
                    Resource.Success(reservationResponse.reservationId) // Use reservationId instead of id
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
            val request = ReserveRequest(
                hotelId = hotelId,
                roomId = roomId,
                startDate = "", // Not used for cancellation
                endDate = "",   // Not used for cancellation
                guestName = "", // Not used for cancellation
                guestEmail = ""  // Not used for cancellation
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
}