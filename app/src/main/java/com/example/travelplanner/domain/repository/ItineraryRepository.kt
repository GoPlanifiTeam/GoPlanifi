package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.ItineraryItem

interface ItineraryRepository {
    fun addActivity(activity: ItineraryItem): Boolean
    fun updateActivity(updatedActivity: ItineraryItem): Boolean
    fun deleteActivity(activityId: String): Boolean
    fun getActivities(): List<ItineraryItem>
    fun getActivitiesByTripId(tripId: String): List<ItineraryItem>
}