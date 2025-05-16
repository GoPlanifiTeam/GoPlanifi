package com.example.goplanify.data.remote.api

import com.example.goplanify.data.remote.dto.AvailabilityResponseDto
import com.example.goplanify.data.remote.dto.HotelDto
import com.example.goplanify.data.remote.dto.ReservationDto
import com.example.goplanify.data.remote.dto.ReservationResponseDto
import com.example.goplanify.data.remote.dto.ReserveRequestDto
import retrofit2.Response
import retrofit2.http.*

interface HotelApiService {
    @GET("hotels/{group_id}/availability")
    suspend fun getHotelAvailability(
        @Path("group_id") groupId: String = "G02",
        @Query("city") city: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("guests") guests: Int
    ): Response<AvailabilityResponseDto>

    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(
        @Path("group_id") groupId: String = "G02"
    ): Response<List<HotelDto>>

    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String = "G02",
        @Body request: ReserveRequestDto
    ): Response<ReservationResponseDto>

    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String = "G02",
        @Body request: ReserveRequestDto
    ): Response<Any>

    @GET("hotels/{group_id}/list_reservations")
    suspend fun getReservations(
        @Path("group_id") groupId: String = "G02",
        @Query("guest_email") guestEmail: String? = null
    ): Response<List<ReservationDto>>
}