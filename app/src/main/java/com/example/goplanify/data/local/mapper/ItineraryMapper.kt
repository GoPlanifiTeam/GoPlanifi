package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.dao.ItineraryImageDao
import com.example.goplanify.data.local.entity.ItineraryItemEntity
import com.example.goplanify.domain.model.ItineraryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun ItineraryItem.toEntity(): ItineraryItemEntity =
    ItineraryItemEntity(
        id = id,
        tripId = trip,
        name = name,
        location = location,
        startDate = dateFormat.parse(startDate) ?: Date(),
        endDate = dateFormat.parse(endDate) ?: Date(),
        order = 0 // Puedes modificar según tu lógica
    )

// Esta función ya no puede ser una extensión simple porque necesita acceso al DAO
// para cargar las imágenes asociadas
suspend fun ItineraryItemEntity.toDomainWithImages(imageDao: ItineraryImageDao): ItineraryItem {
    // Obtener las imágenes de este itinerario de la base de datos
    val images = withContext(Dispatchers.IO) {
        imageDao.getImagesForItinerary(id).map { it.toDomain() }
    }

    return ItineraryItem(
        id = id,
        trip = tripId,
        name = name,
        location = location,
        startDate = dateFormat.format(startDate),
        endDate = dateFormat.format(endDate),
        images = if (images.isEmpty()) null else images
    )
}

// Mantener la función original para compatibilidad, pero sin imágenes
fun ItineraryItemEntity.toDomain(): ItineraryItem =
    ItineraryItem(
        id = id,
        trip = tripId,
        name = name,
        location = location,
        startDate = dateFormat.format(startDate),
        endDate = dateFormat.format(endDate),
        images = null // No podemos cargar imágenes aquí sin el DAO
    )