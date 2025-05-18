package com.example.goplanify.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.data.local.mapper.toDomain
import com.example.goplanify.domain.model.Reservation
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.domain.model.User
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
    val errorMessage: String? = null,
    val isOfflineMode: Boolean = false,
    val linkedTrips: Map<String, Trip> = emptyMap() // Map of reservation ID to linked Trip
)

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationsUIState())
    val uiState: StateFlow<ReservationsUIState> = _uiState

    // Current user state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Function to update current user
    fun updateCurrentUser(user: User?) {
        _currentUser.value = user
        // Reload reservations when user changes
        if (user != null) {
            loadReservations()
        }
    }

    fun loadReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Get current user email
                val email = _currentUser.value?.email

                if (email != null) {
                    // Load reservations from local database for the current user
                    val localReservations = hotelRepository.getLocalReservations(email)

                    // Convert to domain models for display
                    val domainReservations = localReservations.map { it.toDomain() }

                    _uiState.update {
                        it.copy(
                            reservations = domainReservations,
                            isLoading = false
                        )
                    }
                } else {
                    // No user logged in
                    _uiState.update {
                        it.copy(
                            reservations = emptyList(),
                            isLoading = false,
                            errorMessage = "No user logged in"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ReservationsViewModel", "Error loading reservations: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading reservations: ${e.message}"
                    )
                }
            }
        }
    }

    fun cancelReservation(reservation: Reservation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Cancel on the server
                val result = hotelRepository.cancelReservation(reservation.id)

                when (result) {
                    is Resource.Success -> {
                        // If cancellation succeeds, delete from local database
                        hotelRepository.deleteLocalReservation(reservation.id)
                        loadReservations() // Reload to update UI
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

    // Method to cancel reservation and related trip
    fun cancelReservationWithLinkedTrip(reservation: Reservation, tripViewModel: TripViewModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Cancel on the server
                val result = hotelRepository.cancelReservation(reservation.id)

                when (result) {
                    is Resource.Success -> {
                        // If cancellation succeeds, delete from local database
                        hotelRepository.deleteLocalReservation(reservation.id)

                        // Also delete the linked trip
                        tripViewModel.deleteTripByReservationId(reservation.id)

                        loadReservations() // Reload to update UI
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

    // Trip integration functions
    fun assignReservationToTrip(reservationId: String, tripId: String) {
        viewModelScope.launch {
            try {
                hotelRepository.assignReservationToTrip(reservationId, tripId)
                loadReservations() // Reload to update UI
            } catch (e: Exception) {
                Log.e("ReservationsViewModel", "Error assigning reservation to trip: ${e.message}")
                _uiState.update {
                    it.copy(errorMessage = "Error assigning to trip: ${e.message}")
                }
            }
        }
    }

    fun removeReservationFromTrip(reservationId: String) {
        viewModelScope.launch {
            try {
                hotelRepository.removeReservationFromTrip(reservationId)
                loadReservations() // Reload to update UI
            } catch (e: Exception) {
                Log.e("ReservationsViewModel", "Error removing reservation from trip: ${e.message}")
                _uiState.update {
                    it.copy(errorMessage = "Error removing from trip: ${e.message}")
                }
            }
        }
    }

    // Function to update linked trips in the UI state
    fun updateLinkedTrips(trips: List<Trip>) {
        val linkedTripsMap = trips
            .filter { it.linkedReservationId != null }
            .associateBy { it.linkedReservationId!! }

        _uiState.update { it.copy(linkedTrips = linkedTripsMap) }
    }
}