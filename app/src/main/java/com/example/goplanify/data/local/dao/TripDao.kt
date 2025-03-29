package com.example.goplanify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goplanify.data.local.entity.TripEntity

@Dao
interface TripDao {



    @Query("SELECT * FROM trips")
    suspend fun getAllTrips(): List<TripEntity>

    @Query("SELECT * FROM trips WHERE userId = :userId")
    suspend fun getTripsByUser(userId: String): List<TripEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrips(trips: List<TripEntity>)

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: String): Int
}
