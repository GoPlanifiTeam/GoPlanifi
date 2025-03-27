package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.entity.ItineraryItemEntity
import com.example.goplanify.data.local.entity.TripEntity
import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.model.Trip

class Mappers {
    // Trip mappers
    fun mapTripEntityToDomain(entity: TripEntity): Trip {
        return Trip(
            id = entity.id,
            title = entity.title,
            destination = entity.destination,
            startDate = entity.startDate,
            endDate = entity.endDate,
            description = entity.description,
            imageUrl = entity.imageUrl
        )
    }

    fun mapTripDomainToEntity(domain: Trip): TripEntity {
        return TripEntity(
            id = domain.id,
            title = domain.title,
            destination = domain.destination,
            startDate = domain.startDate,
            endDate = domain.endDate,
            description = domain.description,
            imageUrl = domain.imageUrl
        )
    }

    // ItineraryItem mappers
    fun mapItineraryItemEntityToDomain(entity: ItineraryItemEntity): ItineraryItem {
        return ItineraryItem(
            id = entity.id,
            tripId = entity.tripId,
            title = entity.title,
            description = entity.description,
            location = entity.location,
            date = entity.date,
            startTime = entity.startTime,
            endTime = entity.endTime,
            type = entity.type
        )
    }

    fun mapItineraryItemDomainToEntity(domain: ItineraryItem): ItineraryItemEntity {
        return ItineraryItemEntity(
            id = domain.id,
            tripId = domain.tripId,
            title = domain.title,
            description = domain.description,
            location = domain.location,
            date = domain.date,
            startTime = domain.startTime,
            endTime = domain.endTime,
            type = domain.type
        )
    }
}