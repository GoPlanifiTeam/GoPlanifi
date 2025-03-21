package com.example.goplanify.domain.repository
import java.util.Collections.emptyList
import com.example.goplanify.domain.model.Trip

public class AIRecommendationsRepository {
    fun getRecommendations(trip:Trip): List<String>
    {
        // @TODO Implement AI-based recommendation logic
        return emptyList()
    }
}
