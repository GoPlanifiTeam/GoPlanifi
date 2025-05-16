package com.example.goplanify.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.model.Reservation
import com.example.goplanify.domain.model.Rooms
import com.example.goplanify.domain.repository.HotelRepository
import com.example.goplanify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationsUIState(
    val reservations: List<Reservation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationsUIState())
    val uiState: StateFlow<ReservationsUIState> = _uiState

    // Email del usuario para filtrar reservas
    private val userEmail = "tu@email.com" // Cambia por el email del usuario

    // Lista para almacenar las reservas
    private val reservations = mutableListOf<Reservation>()

    fun loadReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Ya que no hay un método getReservations directo,
            // usaremos el método getHotels para simular reservas para la demo
            try {
                val result = hotelRepository.getHotels("G02")

                when (result) {
                    is Resource.Success -> {
                        // Simular algunas reservas para demostración
                        val hotels = result.data ?: emptyList()
                        val demoReservations = createDemoReservations(hotels)

                        _uiState.update {
                            it.copy(
                                reservations = demoReservations,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error loading reservations",
                                isLoading = false
                            )
                        }
                    }
                    else -> {
                        Log.d("ReservationsViewModel", "Unhandled Resource state")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e("ReservationsViewModel", "Error: ${e.message}")
                _uiState.update {
                    it.copy(
                        errorMessage = "Error: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    // Método para crear reservas de demostración usando el modelo Reservation existente
    private fun createDemoReservations(hotels: List<Hotel>): List<Reservation> {
        val demoReservations = mutableListOf<Reservation>()

        // Crear algunas reservas de ejemplo basadas en los hoteles disponibles
        hotels.forEachIndexed { index, hotel ->
            if (hotel.rooms?.isNotEmpty() == true) {
                val room = hotel.rooms?.first() ?: return@forEachIndexed

                demoReservations.add(
                    Reservation(
                        id = "res_${index + 1}",
                        hotelId = hotel.id,
                        roomId = room.id,
                        startDate = "2023-06-01",
                        endDate = "2023-06-05",
                        guestName = "Tu Nombre",
                        guestEmail = userEmail,
                        hotel = hotel,
                        room = room
                    )
                )
            }
        }

        return demoReservations
    }

    fun cancelReservation(reservation: Reservation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val result = hotelRepository.cancelReservation(
                    groupId = "G02",
                    hotelId = reservation.hotelId,
                    roomId = reservation.roomId,
                    reservationId = reservation.id
                )

                when (result) {
                    is Resource.Success -> {
                        // Si se cancela correctamente, actualizar la lista de reservas
                        _uiState.update {
                            it.copy(
                                reservations = it.reservations.filter { r -> r.id != reservation.id },
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error cancelling reservation",
                                isLoading = false
                            )
                        }
                    }
                    else -> {
                        Log.d("ReservationsViewModel", "Unhandled Resource state")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e("ReservationsViewModel", "Error: ${e.message}")
                _uiState.update {
                    it.copy(
                        errorMessage = "Error: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}