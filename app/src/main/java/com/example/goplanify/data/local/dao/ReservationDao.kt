package com.example.goplanify.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goplanify.data.local.entity.ReservationEntity

@Dao
interface ReservationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Query("SELECT * FROM reservations WHERE guestEmail = :email")
    suspend fun getReservationsForUser(email: String): List<ReservationEntity>

    @Query("DELETE FROM reservations WHERE id = :reservationId")
    suspend fun deleteReservation(reservationId: String)

    @Query("SELECT * FROM reservations WHERE tripId = :tripId")
    suspend fun getReservationsForTrip(tripId: String): List<ReservationEntity>

    @Query("UPDATE reservations SET tripId = :tripId WHERE id = :reservationId")
    suspend fun assignReservationToTrip(reservationId: String, tripId: String)

    @Query("UPDATE reservations SET tripId = NULL WHERE id = :reservationId")
    suspend fun removeReservationFromTrip(reservationId: String)
}