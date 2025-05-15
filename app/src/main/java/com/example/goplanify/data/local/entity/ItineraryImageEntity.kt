package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "itinerary_images",
    foreignKeys = [
        ForeignKey(
            entity = ItineraryItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itineraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("itineraryId")] // Índice para mejorar el rendimiento de las consultas
)
data class ItineraryImageEntity(
    @PrimaryKey
    val id: String,
    val itineraryId: String, // ID del itinerario al que pertenece esta imagen
    val imagePath: String,   // Ruta del archivo en el almacenamiento local
    val title: String? = null,  // Título opcional para la imagen
    val description: String? = null // Descripción opcional para la imagen
)