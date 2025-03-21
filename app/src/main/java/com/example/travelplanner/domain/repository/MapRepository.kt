package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Map

class MapRepository {
    private val savedLocations = mutableListOf<Map>()

    fun saveLocation(map: Map) {
        savedLocations.add(map)
    }

    fun getLocations(): List<Map> {
        return savedLocations
    }

    fun findLocationByCoordinates(lat: Double, long: Double): Map? {
        return savedLocations.find { it.latitud == lat && it.longitud == long }
    }
}
