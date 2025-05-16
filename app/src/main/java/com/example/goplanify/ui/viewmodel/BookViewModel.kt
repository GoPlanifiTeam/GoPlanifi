package com.example.goplanify.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.repository.HotelRepository
import com.example.goplanify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BookUIState(
    val loading: Boolean = false,
    val cityMenuExpanded: Boolean = false,
    val city: String = "Barcelona",
    val startDate: LocalDate? = LocalDate.now(),
    val endDate: LocalDate? = LocalDate.now().plusDays(1),
    val hotels: List<Hotel> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class BookViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    // Grupo ID para la API
    val groupId = "G02" // Puedes cambiarlo por BuildConfig.GROUP_ID si lo tienes definido

    private val _uiState = MutableStateFlow(BookUIState())
    val uiState: StateFlow<BookUIState> = _uiState

    fun getGroupId() = groupId

    /* ---------- city picker ---------- */
    fun toggleCityMenu() = _uiState.update { it.copy(cityMenuExpanded = !it.cityMenuExpanded) }

    fun selectCity(city: String) = _uiState.update {
        it.copy(city = city, cityMenuExpanded = false)
    }

    /* ---------- date pickers ---------- */
    fun setStartDate(date: LocalDate) = _uiState.update {
        it.copy(
            startDate = date,
            // Si la fecha fin es anterior a la nueva fecha inicio, actualizar
            endDate = if (it.endDate?.isBefore(date) == true) date.plusDays(1) else it.endDate
        )
    }

    fun setEndDate(date: LocalDate) = _uiState.update {
        it.copy(endDate = date)
    }

    /* ---------- search ---------- */
    fun searchHotels() = viewModelScope.launch {
        val startDate = _uiState.value.startDate ?: return@launch
        val endDate = _uiState.value.endDate ?: return@launch
        val formatter = DateTimeFormatter.ISO_DATE
        val city = _uiState.value.city

        _uiState.update { it.copy(loading = true, errorMessage = null) }

        try {
            val result = hotelRepository.getHotelAvailability(
                destination = city,
                checkIn = startDate.format(formatter),
                checkOut = endDate.format(formatter),
                guests = 2,
                groupId = groupId
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            hotels = result.data ?: emptyList(),
                            loading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.message ?: "Unknown error",
                            loading = false,
                            hotels = emptyList()
                        )
                    }
                }
                else -> {
                    Log.d("BookViewModel", "Unhandled Resource state: $result")
                    _uiState.update { it.copy(loading = false) }
                }
            }
        } catch (e: HttpException) {
            Log.e("BookViewModel", "HTTP error: ${e.message}")
            _uiState.update {
                it.copy(
                    errorMessage = "Error: ${e.code()}: ${e.message()}",
                    loading = false,
                    hotels = emptyList()
                )
            }
        } catch (e: Exception) {
            Log.e("BookViewModel", "Error: ${e.localizedMessage}")
            _uiState.update {
                it.copy(
                    errorMessage = "Error: ${e.message}",
                    loading = false,
                    hotels = emptyList()
                )
            }
        }
    }
}