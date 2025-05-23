package com.example.goplanify.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Image(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tripId: String, // ID del viaje al que pertenece esta imagen
    val imagePath: String, // Ruta de la imagen en el almacenamiento local
    val title: String? = null, // Título opcional para la imagen
    val description: String? = null // Descripción opcional
)