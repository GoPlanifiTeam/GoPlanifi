package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.ItineraryDao
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.dao.UserDao
import com.example.goplanify.data.local.mapper.toDomain
import com.example.goplanify.data.local.mapper.toEntity
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.repository.TripRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val userDao: UserDao,
    private val itineraryDao: ItineraryDao
) : TripRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun getTripsByUser(userId: String): List<Trip> {
        return try {
            val tripEntities = tripDao.getTripsByUser(userId)
            val userEntity = userDao.getUserById(userId)

            val userTrips = tripEntities.map { tripEntity ->
                val itineraryEntities = itineraryDao.getItineraryItemsByTripId(tripEntity.id)
                val itineraries = itineraryEntities.map { it.toDomain() }
                tripEntity.toDomain(null, itineraries)
            }

            val user = userEntity?.toDomain(userTrips)

            val trips = tripEntities.map { tripEntity ->
                val itineraryEntities = itineraryDao.getItineraryItemsByTripId(tripEntity.id)
                val itineraries = itineraryEntities.map { it.toDomain() }
                tripEntity.toDomain(user, itineraries)
            }

            Log.d("DB-Trip", "Fetched ${trips.size} trips for userId=$userId")
            trips
        } catch (e: Exception) {
            Log.e("DB-Trip", "Error fetching trips for userId=$userId", e)
            emptyList()
        }
    }

    override suspend fun getTripById(tripId: String): Result<Trip> {
        return try {
            val result = tripDao.getTripById(tripId)
            if (result != null) {
                Log.d("DB-Trip", "Fetched trip by id=$tripId")
                Result.success(result.toDomain())
            } else {
                Log.w("DB-Trip", "Trip not found: $tripId")
                Result.failure(IllegalArgumentException("Trip not found"))
            }
        } catch (e: Exception) {
            Log.e("DB-Trip", "Error fetching trip by id=$tripId", e)
            Result.failure(e)
        }
    }

    override suspend fun addTrip(trip: Trip): Result<Trip> {
        return try {
            val userId = trip.user?.userId ?: return Result.failure(IllegalArgumentException("User is required"))

            // Validar nombre duplicado de viaje para el mismo usuario
            val existingTrips = tripDao.getTripsByUser(userId)
            // Validar que no exista un viaje al mismo destino con mismas fechas
            val newStart = dateFormat.parse(trip.startDate)
            val newEnd = dateFormat.parse(trip.endDate)

            val overlappingTrip = existingTrips.any {
                it.destination.equals(trip.destination, ignoreCase = true) &&
                        it.startDate == newStart &&
                        it.endDate == newEnd
            }

            if (overlappingTrip) {
                Log.w("Validation-Trip", "Trip with same destination and dates already exists: ${trip.destination}")
                return Result.failure(IllegalArgumentException("A trip to this destination with the same dates already exists"))
            }

            // Validar fechas del viaje
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = formatter.parse(trip.startDate)
            val end = formatter.parse(trip.endDate)

            if (start.after(end)) {
                Log.w("Validation-Trip", "Invalid date range: ${trip.startDate} > ${trip.endDate}")
                return Result.failure(IllegalArgumentException("Start date must be before or equal to end date"))
            }

            // Validar que los itinerarios estén dentro del rango de fechas del viaje
            val allDatesValid = trip.itineraries.all {
                val itemStart = formatter.parse(it.startDate)
                itemStart in start..end
            }

            if (!allDatesValid) {
                Log.w("Validation-Trip", "Itinerary dates are out of trip range")
                return Result.failure(IllegalArgumentException("Itinerary dates must be within the trip date range"))
            }

            // Insertar el viaje
            val tripEntity = trip.toEntity()
            tripDao.insertTrip(tripEntity)
            Log.d("DB-Trip", "Inserted trip ${trip.id} for user $userId")

            // Insertar itinerarios
            val itineraryEntities = trip.itineraries.map { it.toEntity() }
            itineraryEntities.forEach {
                itineraryDao.insertItineraryItem(it)
                Log.d("DB-Trip", "Inserted itinerary ${it.id} for trip ${it.tripId}")
            }

            Result.success(trip)
        } catch (e: Exception) {
            Log.e("DB-Trip", "Error adding trip ${trip.id}", e)
            Result.failure(e)
        }
    }


    override suspend fun updateTrip(trip: Trip): Result<Trip> {
        return try {
            tripDao.updateTrip(trip.toEntity())
            Log.d("DB-Trip", "Updated trip ${trip.id}")
            Result.success(trip)
        } catch (e: Exception) {
            Log.e("DB-Trip", "Error updating trip ${trip.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteTrip(tripId: String): Result<Boolean> {
        return try {
            val deleted = tripDao.deleteTripById(tripId)
            Log.d("DB-Trip", "Deleted trip $tripId → success=${deleted > 0}")
            Result.success(deleted > 0)
        } catch (e: Exception) {
            Log.e("DB-Trip", "Error deleting trip $tripId", e)
            Result.failure(e)
        }
    }
}
