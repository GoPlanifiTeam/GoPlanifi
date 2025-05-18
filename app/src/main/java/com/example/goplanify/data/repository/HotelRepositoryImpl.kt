package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.ReservationDao
import com.example.goplanify.data.local.entity.ReservationEntity
import com.example.goplanify.data.remote.api.HotelApiService
import com.example.goplanify.data.remote.dto.ReservationResponseDto
import com.example.goplanify.data.remote.dto.ReserveRequestDto
import com.example.goplanify.data.remote.mapper.toDomain
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.model.Reservation
import com.example.goplanify.domain.repository.HotelRepository
import com.example.goplanify.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val apiService: HotelApiService,
    private val reservationDao: ReservationDao
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
    ): Resource<ReservationResponseDto> {
        return try {
            val request = ReserveRequestDto(
                hotel_id = hotelId,
                room_id = roomId,
                start_date = startDate,
                end_date = endDate,
                guest_name = guestName,
                guest_email = guestEmail
            )

            val response = apiService.reserveRoom(groupId, request) // Use apiService instead of api

            if (response.isSuccessful) {
                val reservationResponse = response.body()
                if (reservationResponse != null) {
                    Resource.Success(reservationResponse)
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    override suspend fun cancelReservation(reservationId: String): Resource<Reservation> {
        return try {
            val response = apiService.cancelReservation(reservationId)

            if (response.isSuccessful) {
                val cancelledReservation = response.body()
                if (cancelledReservation != null) {
                    Log.d(TAG, "Successfully cancelled reservation $reservationId")
                    Resource.Success(cancelledReservation.toDomain())
                } else {
                    Log.e(TAG, "Response body is null for cancellation")
                    Resource.Error("Response body is null")
                }
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

    override suspend fun getReservations(
        groupId: String,
        guestEmail: String?
    ): Resource<List<Reservation>> {
        return try {
            val response = apiService.getReservations(groupId, guestEmail)
            if (response.isSuccessful) {
                val reservations = response.body()
                if (reservations != null) {
                    Resource.Success(reservations.map { it.toDomain() })
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                Resource.Error("Error: ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            handleException(e, "getting reservations")
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
    override suspend fun saveReservationLocally(reservation: ReservationEntity) {
        reservationDao.insertReservation(reservation)
    }

    override suspend fun getLocalReservations(email: String): List<ReservationEntity> {
        return reservationDao.getReservationsForUser(email)
    }

    override suspend fun deleteLocalReservation(reservationId: String) {
        reservationDao.deleteReservation(reservationId)
    }

    override suspend fun assignReservationToTrip(reservationId: String, tripId: String) {
        reservationDao.assignReservationToTrip(reservationId, tripId)
    }

    override suspend fun removeReservationFromTrip(reservationId: String) {
        reservationDao.removeReservationFromTrip(reservationId)
    }

    override suspend fun getReservationsForTrip(tripId: String): List<ReservationEntity> {
        return reservationDao.getReservationsForTrip(tripId)
    }

}