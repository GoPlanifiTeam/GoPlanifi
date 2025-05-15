package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.ItineraryDao
import com.example.goplanify.data.local.dao.ItineraryImageDao
import com.example.goplanify.data.local.mapper.toDomain
import com.example.goplanify.data.local.mapper.toDomainList
import com.example.goplanify.data.local.mapper.toEntity
import com.example.goplanify.data.local.mapper.toEntityList
import com.example.goplanify.domain.model.ItineraryImage
import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.repository.ItineraryRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryRepositoryImpl @Inject constructor(
    private val dao: ItineraryDao,
    private val imageDao: ItineraryImageDao // Ahora recibe el DAO de imágenes
) : ItineraryRepository {

    override suspend fun addItineraryItem(item: ItineraryItem): Result<ItineraryItem> {
        return try {
            // Primero guardar el itinerario
            dao.insertItineraryItem(item.toEntity())
            Log.d("DB-Itinerary", "Itinerary inserted: ${item.id} for trip ${item.trip}")

            // Si hay imágenes, guardarlas también
            item.images?.let { images ->
                if (images.isNotEmpty()) {
                    // Convertir a entidades y guardar
                    val imageEntities = images.toEntityList()
                    imageDao.insertItineraryImages(imageEntities)
                    Log.d("DB-Itinerary", "Added ${images.size} images to itinerary ${item.id}")
                }
            }

            Result.success(item)
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error inserting itinerary: ${item.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteItineraryItem(itineraryId: String): Result<Boolean> {
        return try {
            // Las imágenes se eliminarán automáticamente debido a la relación de cascada
            dao.deleteItineraryItemById(itineraryId)
            Log.d("DB-Itinerary", "Itinerary deleted: $itineraryId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error deleting itinerary: $itineraryId", e)
            Result.failure(e)
        }
    }

    override suspend fun getItineraryItemsByTripId(tripId: String): List<ItineraryItem> {
        return try {
            val itineraryEntities = dao.getItineraryItemsByTripId(tripId)

            // Convertir entidades a modelos de dominio y cargar sus imágenes
            val itineraryItems = itineraryEntities.map { entity ->
                val itineraryId = entity.id
                val images = imageDao.getImagesForItinerary(itineraryId).toDomainList()

                // Convertir la entidad a modelo de dominio e incluir las imágenes
                val item = entity.toDomain()

                // Como la función toDomain() básica no incluye imágenes, necesitamos crear un nuevo objeto
                // con las imágenes cargadas
                item.copy(images = if (images.isEmpty()) null else images)
            }

            Log.d("DB-Itinerary", "Fetched ${itineraryItems.size} itineraries for tripId=$tripId")
            itineraryItems
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error fetching itineraries for tripId=$tripId", e)
            emptyList()
        }
    }

    override suspend fun updateItineraryDates(itineraryId: String, startDate: String, endDate: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = formatter.parse(startDate) ?: Date()
        val end = formatter.parse(endDate) ?: Date()
        try {
            dao.updateItineraryItemDates(itineraryId, start, end)
            Log.d("DB-Itinerary", "Updated dates for itinerary $itineraryId → $start to $end")
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error updating dates for itinerary $itineraryId", e)
        }
    }

    // Nuevos métodos para manejar imágenes de itinerario

    override suspend fun addImagesForItinerary(itineraryId: String, images: List<ItineraryImage>): Result<Boolean> {
        return try {
            if (images.isNotEmpty()) {
                val imageEntities = images.toEntityList()
                imageDao.insertItineraryImages(imageEntities)
                Log.d("DB-Itinerary", "Added ${images.size} images to itinerary $itineraryId")
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error adding images to itinerary $itineraryId", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteImageFromItinerary(imageId: String): Result<Boolean> {
        return try {
            imageDao.deleteItineraryImageById(imageId)
            Log.d("DB-Itinerary", "Deleted image $imageId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error deleting image $imageId", e)
            Result.failure(e)
        }
    }

    override suspend fun getImagesForItinerary(itineraryId: String): List<ItineraryImage> {
        return try {
            val images = imageDao.getImagesForItinerary(itineraryId).toDomainList()
            Log.d("DB-Itinerary", "Fetched ${images.size} images for itinerary $itineraryId")
            images
        } catch (e: Exception) {
            Log.e("DB-Itinerary", "Error fetching images for itinerary $itineraryId", e)
            emptyList()
        }
    }
}