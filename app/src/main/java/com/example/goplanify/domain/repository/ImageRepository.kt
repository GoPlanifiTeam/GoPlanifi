
package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Image
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun addImage(tripId: String, imagePath: String, title: String? = null, description: String? = null): String
    suspend fun addImages(images: List<Image>)
    suspend fun updateImage(image: Image)
    suspend fun deleteImage(imageId: String)
    suspend fun deleteAllImagesForTrip(tripId: String)
    fun getImagesForTrip(tripId: String): Flow<List<Image>>
    suspend fun getImageById(imageId: String): Image?
}