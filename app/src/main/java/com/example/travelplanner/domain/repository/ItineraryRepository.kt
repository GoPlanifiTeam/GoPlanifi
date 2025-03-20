package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.ItineraryItem

class ItineraryRepository {

    // Modify this method to fetch itineraries by tripId (String)
    fun getItineraryItemsByTripId(tripId: String): List<ItineraryItem> {
        // Here you can implement your actual logic to fetch itineraries based on the tripId
        // For example, you can query a database, call an API, or use hardcoded data for now.

        // Example of hardcoded data for itineraries
        return when (tripId) {
            "1" -> listOf(
                ItineraryItem(id = "1", name = "Explore the Eiffel Tower", location = "Eiffel Tower", startDate = "2025-06-01", endDate = "2025-06-03", trip = tripId),
                ItineraryItem(id = "2", name = "Day trip to Versailles", location = "Versailles", startDate = "2025-06-04", endDate = "2025-06-05", trip = tripId)
            )
            "2" -> listOf(
                ItineraryItem(id = "1", name = "Visit the Senso-ji Temple", location = "Senso-ji Temple", startDate = "2025-07-01", endDate = "2025-07-03", trip = tripId),
                ItineraryItem(id = "2", name = "Explore Akihabara", location = "Akihabara", startDate = "2025-07-04", endDate = "2025-07-06", trip = tripId)
            )
            else -> emptyList() // No itineraries for unknown tripIds
        }
    }

    // Optionally, if you have functionality to add or delete itinerary items, you can add those here.
}
