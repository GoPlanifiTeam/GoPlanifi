package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Image
import com.example.travelplanner.domain.model.Trip

class ImageRepository {
    private val imageDatabase = mutableListOf<Image>()

    // Fetch images for a specific trip
    fun getImages(trip: Trip): List<Image> {
        return imageDatabase.filter { it.trip == trip }
    }

    // Add image for a specific trip
    fun addImage(trip: Trip, imageUrl: String) {
        val newImage = Image(id = imageDatabase.size + 1, trip = trip, imageURL = imageUrl)
        imageDatabase.add(newImage)
    }

    // Delete image by image ID
    fun deleteImage(imageId: Int) {
        imageDatabase.removeIf { it.id == imageId }
    }
}
