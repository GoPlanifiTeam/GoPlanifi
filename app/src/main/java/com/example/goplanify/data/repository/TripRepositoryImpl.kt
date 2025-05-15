package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.ItineraryDao
import com.example.goplanify.data.local.dao.ItineraryImageDao
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.dao.UserDao
import com.example.goplanify.data.local.entity.ItineraryImageEntity
import com.example.goplanify.data.local.mapper.toDomain
import com.example.goplanify.data.local.mapper.toDomainList
import com.example.goplanify.data.local.mapper.toEntity
import com.example.goplanify.data.local.mapper.toEntityList
import com.example.goplanify.domain.model.ItineraryImage
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
    private val itineraryDao: ItineraryDao,
    private val itineraryImageDao: ItineraryImageDao // Agregado: DAO para imágenes
) : TripRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val TAG = "TripRepository" // Para logs consistentes

    override suspend fun getTripsByUser(userId: String): List<Trip> {
        return try {
            Log.d(TAG, "Buscando viajes para el usuario: $userId")

            val tripEntities = tripDao.getTripsByUser(userId)
            val userEntity = userDao.getUserById(userId)

            // Primero obtener los viajes del usuario
            val userTrips = tripEntities.map { tripEntity ->
                val itineraryEntities = itineraryDao.getItineraryItemsByTripId(tripEntity.id)

                // Para cada itinerario, obtener sus imágenes
                val itineraries = itineraryEntities.map { itineraryEntity ->
                    val itineraryId = itineraryEntity.id

                    // Obtener las imágenes del itinerario
                    val imageEntities = itineraryImageDao.getImagesForItinerary(itineraryId)

                    // Convertir el itinerario a modelo de dominio
                    val basicItinerary = itineraryEntity.toDomain()

                    // Añadir las imágenes al itinerario si existen
                    if (imageEntities.isNotEmpty()) {
                        val images = imageEntities.map { imageEntity ->
                            ItineraryImage(
                                id = imageEntity.id,
                                itineraryId = imageEntity.itineraryId,
                                imagePath = imageEntity.imagePath,
                                title = imageEntity.title,
                                description = imageEntity.description
                            )
                        }
                        basicItinerary.copy(images = images)
                    } else {
                        basicItinerary // Sin imágenes
                    }
                }

                tripEntity.toDomain(null, itineraries)
            }

            val user = userEntity?.toDomain(userTrips)

            // Ahora obtener los viajes con el usuario completo
            val trips = tripEntities.map { tripEntity ->
                val itineraryEntities = itineraryDao.getItineraryItemsByTripId(tripEntity.id)

                // Para cada itinerario, obtener sus imágenes (mismo código que arriba)
                val itineraries = itineraryEntities.map { itineraryEntity ->
                    val itineraryId = itineraryEntity.id

                    // Obtener las imágenes del itinerario
                    val imageEntities = itineraryImageDao.getImagesForItinerary(itineraryId)

                    // Convertir el itinerario a modelo de dominio
                    val basicItinerary = itineraryEntity.toDomain()

                    // Añadir las imágenes al itinerario si existen
                    if (imageEntities.isNotEmpty()) {
                        val images = imageEntities.map { imageEntity ->
                            ItineraryImage(
                                id = imageEntity.id,
                                itineraryId = imageEntity.itineraryId,
                                imagePath = imageEntity.imagePath,
                                title = imageEntity.title,
                                description = imageEntity.description
                            )
                        }
                        basicItinerary.copy(images = images)
                    } else {
                        basicItinerary // Sin imágenes
                    }
                }

                tripEntity.toDomain(user, itineraries)
            }

            Log.d(TAG, "Obtenidos ${trips.size} viajes para el usuario $userId")
            trips
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener viajes del usuario $userId: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getTripById(tripId: String): Result<Trip> {
        return try {
            val result = tripDao.getTripById(tripId)
            if (result != null) {
                Log.d(TAG, "Fetched trip by id=$tripId")
                Result.success(result.toDomain())
            } else {
                Log.w(TAG, "Trip not found: $tripId")
                Result.failure(IllegalArgumentException("Trip not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching trip by id=$tripId", e)
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
            Log.d(TAG, "Insertado viaje ${trip.id} para usuario $userId")

            // Insertar itinerarios con sus imágenes
            for (itinerary in trip.itineraries) {
                // Guardar el itinerario
                val itineraryEntity = itinerary.toEntity()
                itineraryDao.insertItineraryItem(itineraryEntity)
                Log.d(TAG, "Insertado itinerario ${itinerary.id} para viaje ${trip.id}")

                // Guardar las imágenes del itinerario si existen
                itinerary.images?.let { images ->
                    if (images.isNotEmpty()) {
                        Log.d(TAG, "El itinerario ${itinerary.id} tiene ${images.size} imágenes")

                        // Asegurarse de que cada imagen tenga el itineraryId correcto
                        val validatedImages = images.map { image ->
                            if (image.itineraryId != itinerary.id) {
                                Log.w(TAG, "Corrigiendo itineraryId incorrecto: ${image.itineraryId} -> ${itinerary.id}")
                                image.copy(itineraryId = itinerary.id)
                            } else {
                                image
                            }
                        }

                        // Guardar cada imagen
                        for (image in validatedImages) {
                            try {
                                // Convertir a entidad
                                val imageEntity = ItineraryImageEntity(
                                    id = image.id,
                                    itineraryId = image.itineraryId,
                                    imagePath = image.imagePath,
                                    title = image.title,
                                    description = image.description
                                )

                                // Guardar la imagen
                                itineraryImageDao.insertItineraryImage(imageEntity)
                                Log.d(TAG, "Insertada imagen ${image.id} para itinerario ${itinerary.id}")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error al guardar imagen: ${e.message}", e)
                            }
                        }

                        // Verificar que se guardaron las imágenes
                        val savedImages = itineraryImageDao.getImagesForItinerary(itinerary.id)
                        Log.d(TAG, "Verificación: se guardaron ${savedImages.size} imágenes para el itinerario ${itinerary.id}")
                    }
                }
            }

            Result.success(trip)
        } catch (e: Exception) {
            Log.e(TAG, "Error al añadir viaje ${trip.id}: ${e.message}", e)
            Result.failure(e)
        }
    }


    override suspend fun updateTrip(trip: Trip): Result<Trip> {
        return try {
            // Actualizar el viaje
            tripDao.updateTrip(trip.toEntity())
            Log.d(TAG, "Actualizado viaje ${trip.id}")

            // Actualizar los itinerarios y sus imágenes
            for (itinerary in trip.itineraries) {
                // Actualizar el itinerario
                val itineraryEntity = itinerary.toEntity()
                itineraryDao.updateItineraryItem(itineraryEntity)

                // Actualizar las imágenes
                itinerary.images?.let { images ->
                    if (images.isNotEmpty()) {
                        // Primero eliminar las imágenes existentes
                        itineraryImageDao.deleteAllImagesForItinerary(itinerary.id)

                        // Luego insertar las nuevas
                        for (image in images) {
                            // Asegurarse de que el itineraryId sea correcto
                            val validatedImage = if (image.itineraryId != itinerary.id) {
                                image.copy(itineraryId = itinerary.id)
                            } else {
                                image
                            }

                            // Convertir a entidad y guardar
                            val imageEntity = ItineraryImageEntity(
                                id = validatedImage.id,
                                itineraryId = validatedImage.itineraryId,
                                imagePath = validatedImage.imagePath,
                                title = validatedImage.title,
                                description = validatedImage.description
                            )

                            itineraryImageDao.insertItineraryImage(imageEntity)
                        }

                        Log.d(TAG, "Actualizadas ${images.size} imágenes para el itinerario ${itinerary.id}")
                    }
                }
            }

            Result.success(trip)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar viaje ${trip.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteTrip(tripId: String): Result<Boolean> {
        return try {
            // Las imágenes se eliminarán automáticamente debido a la relación de cascada en SQLite
            val deleted = tripDao.deleteTripById(tripId)
            Log.d(TAG, "Eliminado viaje $tripId → éxito=${deleted > 0}")
            Result.success(deleted > 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar viaje $tripId: ${e.message}", e)
            Result.failure(e)
        }
    }

}