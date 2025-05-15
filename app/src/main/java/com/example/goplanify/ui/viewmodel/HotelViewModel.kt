package com.example.goplanify.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.repository.HotelRepository
import com.example.goplanify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val repository: HotelRepository
) : ViewModel() {

    private val _hotels = MutableStateFlow<Resource<List<Hotel>>>(Resource.Loading())
    val hotels: StateFlow<Resource<List<Hotel>>> = _hotels

    fun getHotelAvailability(destination: String, checkIn: String, checkOut: String, guests: Int) {
        viewModelScope.launch {
            _hotels.value = Resource.Loading()
            val result = repository.getHotelAvailability(destination, checkIn, checkOut, guests)
            _hotels.value = result
        }
    }
}
