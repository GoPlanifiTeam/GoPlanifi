package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.domain.repository.ItineraryRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItineraryRepositoryImpl : ItineraryRepository {
    private val activities = mutableListOf<ItineraryItem>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun addActivity(activity: ItineraryItem): Boolean {
        if (!validateActivity(activity)) {
            return false
        }

        activities.add(activity)
        return true
    }

    override fun updateActivity(updatedActivity: ItineraryItem): Boolean {
        if (!validateActivity(updatedActivity)) {
            return false
        }

        val index = activities.indexOfFirst { it.id == updatedActivity.id }
        if (index != -1) {
            activities[index] = updatedActivity
            return true
        }
        return false
    }

    override fun deleteActivity(activityId: String): Boolean {
        val initialSize = activities.size
        activities.removeAll { it.id == activityId }
        return activities.size < initialSize
    }

    override fun getActivities(): List<ItineraryItem> {
        return activities.toList()
    }

    override fun getActivitiesByTripId(tripId: String): List<ItineraryItem> {
        return activities.filter { it.tripId == tripId }
    }

    private fun validateActivity(activity: ItineraryItem): Boolean {
        // Check for required fields
        if (activity.id.isBlank() || activity.tripId.isBlank() ||
            activity.name.isBlank() || activity.location.isBlank() ||
            activity.startDate.isBlank() || activity.endDate.isBlank()) {
            return false
        }

        try {
            // Parse dates
            val today = Date()
            val startDate = dateFormat.parse(activity.startDate)
            val endDate = dateFormat.parse(activity.endDate)

            // Validate dates
            if (startDate == null || endDate == null || startDate.after(endDate)) {
                return false
            }

            // Check if dates are in the future
            if (startDate.before(today)) {
                return false
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }
}