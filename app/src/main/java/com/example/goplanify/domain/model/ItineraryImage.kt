package com.example.goplanify.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Modelo para las imágenes asociadas a un itinerario específico
 */
@Entity(
    tableName = "itinerary_images",
    foreignKeys = [
        ForeignKey(
            entity = ItineraryItem::class,
            parentColumns = ["id"],
            childColumns = ["itineraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ItineraryImage(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val itineraryId: String, // ID del itinerario al que pertenece esta imagen
    val imagePath: String, // Ruta de la imagen en el almacenamiento local
    val title: String? = null, // Título opcional para la imagen
    val description: String? = null // Descripción opcional
)