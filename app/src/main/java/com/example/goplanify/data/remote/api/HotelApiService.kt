package com.example.goplanify.data.remote.api

import com.example.goplanify.data.remote.model.HotelAvailabilityResponse
import com.example.goplanify.data.remote.model.HotelListResponse
import com.example.goplanify.data.remote.model.ReservationResponse
import com.example.goplanify.data.remote.model.ReserveRequest
import retrofit2.Response
import retrofit2.http.*

interface HotelApiService {    @GET("hotels/{group_id}/availability")
    suspend fun getHotelAvailability(
        @Query("city") city: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("guests") guests: Int,
        @Path("group_id") groupId: String = "G02"
    ): Response<HotelAvailabilityResponse>
    
    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(
        @Path("group_id") groupId: String
    ): Response<HotelListResponse>
    
    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): Response<ReservationResponse>
    
    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): Response<Any>
    
    @GET("hotels/{group_id}/reservations")
    suspend fun getReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): Response<List<ReservationResponse>>
}
