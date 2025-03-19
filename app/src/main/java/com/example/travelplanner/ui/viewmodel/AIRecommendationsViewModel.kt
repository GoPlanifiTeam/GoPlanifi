package com.example.travelplanner.ui.viewmodel
import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.AIRecommendationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AIRecommendationsViewModel : ViewModel() {
    private val repository = AIRecommendationsRepository()
    private val _recommendations = MutableStateFlow<List<String>>(emptyList())
    val recommendations: StateFlow<List<String>> get() = _recommendations
    fun fetchRecommendations(trip: Trip) {
        _recommendations.value = repository.getRecommendations(trip)
    }
}
