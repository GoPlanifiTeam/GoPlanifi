package com.example.goplanify.data.repository

import com.example.goplanify.data.local.dao.ImageDao
import com.example.goplanify.domain.model.Image
import com.example.goplanify.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao
) : ImageRepository {

    override suspend fun addImage(
        tripId: String,
        imagePath: String,
        title: String?,
        description: String?
    ): String {
        val imageId = UUID.randomUUID().toString()
        val image = Image(
            id = imageId,
            tripId = tripId,
            imagePath = imagePath,
            title = title,
            description = description
        )
        imageDao.insertImage(image)
        return imageId
    }

    override suspend fun addImages(images: List<Image>) {
        imageDao.insertImages(images)
    }

    override suspend fun updateImage(image: Image) {
        imageDao.updateImage(image)
    }

    override suspend fun deleteImage(imageId: String) {
        imageDao.deleteImageById(imageId)
    }

    override suspend fun deleteAllImagesForTrip(tripId: String) {
        imageDao.deleteAllImagesForTrip(tripId)
    }

    override fun getImagesForTrip(tripId: String): Flow<List<Image>> {
        return imageDao.getImagesForTrip(tripId)
    }

    override suspend fun getImageById(imageId: String): Image? {
        return imageDao.getImageById(imageId)
    }
}