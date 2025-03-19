package com.example.travelplanner.ui.viewmodel
import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.repository.ImageRepository
import com.example.travelplanner.domain.model.Image
import com.example.travelplanner.domain.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ImageViewModel : ViewModel() {
    private val repository = ImageRepository()
    private val _images = MutableStateFlow<List<Image>>(emptyList())
    val images: StateFlow<List<Image>> get() = _images
    fun fetchImages(trip: Trip) {
        _images.value = repository.getImages(trip)
    }
}
