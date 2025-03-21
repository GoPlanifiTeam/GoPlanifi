package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.ItineraryItem
import java.util.UUID

class ItineraryRepository {

    // Lista interna para almacenar itinerarios
    private val itineraries = mutableListOf<ItineraryItem>()

    fun addItineraryItem(tripId: String, activityName: String, location: String): ItineraryItem {
        val newItem = ItineraryItem(
            id = UUID.randomUUID().toString(),
            name = activityName,
            location = location,
            startDate = "2025-01-01",
            endDate = "2025-01-02",
            trip = tripId
        )

        // Agregar el nuevo itinerario a la lista interna
        itineraries.add(newItem)

        return newItem
    }

    // Eliminar un itinerario basado en el id
    fun deleteItineraryItem(itineraryId: String): Boolean {
        val itemToRemove = itineraries.find { it.id == itineraryId }
        return if (itemToRemove != null) {
            itineraries.remove(itemToRemove) // Elimina el itinerario de la lista
            true
        } else {
            false
        }
    }

    // Obtener itinerarios por tripId
    fun getItineraryItemsByTripId(tripId: String): List<ItineraryItem> {
        return itineraries.filter { it.trip == tripId }
    }
}

