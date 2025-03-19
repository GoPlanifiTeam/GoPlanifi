package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Image
import com.example.travelplanner.domain.model.Trip

class ImageRepository {
    private val imageDatabase = mutableListOf<Image>()

    fun getImages(trip: Trip): List<Image> {
        return imageDatabase.filter { it.trip == trip }
    }

    fun addImage(trip: Trip, imageUrl: String) {
        val newImage = Image(trip, id = imageDatabase.size + 1, imageURL = imageUrl)
        imageDatabase.add(newImage)
    }

    fun deleteImage(imageId: Int) {
        imageDatabase.removeIf { it.id == imageId }
    }
}