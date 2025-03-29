package com.example.goplanify.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.repository.ItineraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {

    private val _itineraries = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itineraries: StateFlow<List<ItineraryItem>> get() = _itineraries

    private val _selectedItineraries = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val selectedItineraries: StateFlow<List<ItineraryItem>> get() = _selectedItineraries

    fun toggleItinerarySelection(itinerary: ItineraryItem) {
        _selectedItineraries.update {
            if (it.contains(itinerary)) it.filter { i -> i.id != itinerary.id }
            else it + itinerary
        }
    }

    fun fetchItineraryItems(tripId: String) {
        viewModelScope.launch {
            _itineraries.value = itineraryRepository.getItineraryItemsByTripId(tripId)
        }
    }

    fun addItineraryItem(item: ItineraryItem) {
        viewModelScope.launch {
            itineraryRepository.addItineraryItem(item)
            fetchItineraryItems(item.trip)
        }
    }

    fun updateItineraryDates(itineraryId: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            itineraryRepository.updateItineraryDates(itineraryId, startDate, endDate)
        }
    }


    fun deleteItineraryItem(itineraryId: String, tripId: String) {
        viewModelScope.launch {
            itineraryRepository.deleteItineraryItem(itineraryId)
            fetchItineraryItems(tripId)
        }
    }
}

