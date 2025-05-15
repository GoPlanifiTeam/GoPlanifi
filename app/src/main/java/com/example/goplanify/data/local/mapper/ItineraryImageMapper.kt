package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.entity.ItineraryImageEntity
import com.example.goplanify.domain.model.ItineraryImage

fun ItineraryImage.toEntity(): ItineraryImageEntity {
    return ItineraryImageEntity(
        id = id,
        itineraryId = itineraryId,
        imagePath = imagePath,
        title = title,
        description = description
    )
}

fun ItineraryImageEntity.toDomain(): ItineraryImage {
    return ItineraryImage(
        id = id,
        itineraryId = itineraryId,
        imagePath = imagePath,
        title = title,
        description = description
    )
}

fun List<ItineraryImageEntity>.toDomainList(): List<ItineraryImage> {
    return map { it.toDomain() }
}

fun List<ItineraryImage>.toEntityList(): List<ItineraryImageEntity> {
    return map { it.toEntity() }
}