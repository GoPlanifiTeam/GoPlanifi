package com.example.goplanify.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.Reservation
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
    private val userEmail = "danielgraogg@gmail.com"

    fun loadReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Usar getReservations en lugar de getHotels
                val result = hotelRepository.getReservations("G02", userEmail)

                when (result) {
                    is Resource.Success -> {
                        val reservations = result.data ?: emptyList()
                        _uiState.update {
                            it.copy(
                                reservations = reservations,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error<*> -> {  // Añadido <*> aquí
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
                        // Si se cancela correctamente, recargar la lista para obtener el estado actualizado
                        loadReservations()
                    }
                    is Resource.Error<*> -> {  // Añadido <*> aquí
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