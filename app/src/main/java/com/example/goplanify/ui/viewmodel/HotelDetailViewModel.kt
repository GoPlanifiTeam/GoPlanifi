package com.example.goplanify.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.data.local.entity.ReservationEntity
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.domain.model.ReserveRequest
import com.example.goplanify.domain.model.Rooms
import com.example.goplanify.domain.model.User
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
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class HotelDetailUIState(
    val loading: Boolean = true,
    val hotel: Hotel? = null,
    val rooms: List<Rooms> = emptyList(),
    val selectedRoom: Rooms? = null,
    val errorMessage: String? = null,
    val reservationSuccess: Boolean = false,
    val startDate: String? = null,
    val endDate: String? = null,
    val groupId: String? = null
)

@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // State for current user - updated from the Composable
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Function to update current user from Composable
    fun updateCurrentUser(user: User?) {
        _currentUser.value = user
    }

    private val _uiState = MutableStateFlow(HotelDetailUIState())
    val uiState: StateFlow<HotelDetailUIState> = _uiState

    // Obtener parámetros de navegación si es necesario
    private val hotelId: String? = savedStateHandle.get<String>("hotelId")
    private val groupId: String? = savedStateHandle.get<String>("groupId")
    private val startDate: String? = savedStateHandle.get<String>("startDate")
    private val endDate: String? = savedStateHandle.get<String>("endDate")

    init {
        // Actualizar fechas y grupo en el estado
        _uiState.update {
            it.copy(
                startDate = startDate,
                endDate = endDate,
                groupId = groupId
            )
        }

        // Cargar detalles si tenemos los parámetros
        if (!hotelId.isNullOrEmpty() && !groupId.isNullOrEmpty() &&
            !startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
            loadHotelDetails(hotelId, groupId, startDate, endDate)
        }
    }

    fun loadHotelDetails(
        hotelId: String,
        groupId: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = true,
                    errorMessage = null,
                    startDate = startDate,
                    endDate = endDate,
                    groupId = groupId
                )
            }

            try {
                // Obtener disponibilidad para las fechas especificadas
                val result = hotelRepository.getHotelAvailability(
                    destination = "",  // No importa para detalles específicos
                    checkIn = startDate,
                    checkOut = endDate,
                    guests = 2,
                    groupId = groupId
                )

                when (result) {
                    is Resource.Success -> {
                        val hotels = result.data ?: emptyList()
                        val selectedHotel = hotels.find { it.id == hotelId }

                        if (selectedHotel != null) {
                            _uiState.update {
                                it.copy(
                                    hotel = selectedHotel,
                                    rooms = selectedHotel.rooms ?: emptyList(),
                                    loading = false
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Hotel not found",
                                    loading = false
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error loading hotel details",
                                loading = false
                            )
                        }
                    }
                    else -> {
                        Log.d("HotelDetailViewModel", "Unhandled Resource state: $result")
                        _uiState.update { it.copy(loading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e("HotelDetailViewModel", "Error: ${e.message}")
                _uiState.update {
                    it.copy(
                        errorMessage = "Error: ${e.message}",
                        loading = false
                    )
                }
            }
        }
    }

    fun selectRoom(room: Rooms) {
        _uiState.update { it.copy(selectedRoom = room) }
    }

    fun reserveRoom() {
        val room = _uiState.value.selectedRoom ?: return
        val hotel = _uiState.value.hotel ?: return
        val startDate = _uiState.value.startDate ?: return
        val endDate = _uiState.value.endDate ?: return
        val groupId = _uiState.value.groupId ?: "G02"

        // Get current user information
        val user = _currentUser.value
        val guestName = if (user != null) "${user.firstName} ${user.lastName}" else "Guest"
        val guestEmail = user?.email.toString()

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            try {
                val result = hotelRepository.reserveRoom(
                    groupId = groupId,
                    hotelId = hotel.id,
                    roomId = room.id,
                    startDate = startDate,
                    endDate = endDate,
                    guestName = guestName,
                    guestEmail = guestEmail
                )

                when (result) {
                    is Resource.Success -> {
                        // API reservation successful, now save locally
                        val responseDto = result.data
                        if (responseDto != null) {
                            // Extract reservation info from the response
                            val reservation = responseDto.reservation
                            val nights = responseDto.nights

                            // Calculate total price
                            val totalPrice = room.price * nights

                            // Create local entity
                            val localReservation = ReservationEntity(
                                id = reservation.id,
                                hotelId = hotel.id,
                                roomId = room.id,
                                startDate = startDate,
                                endDate = endDate,
                                guestName = guestName,
                                guestEmail = guestEmail,
                                hotelName = hotel.name,
                                hotelAddress = hotel.address,
                                hotelImageUrl = hotel.imageUrl,
                                roomType = room.roomType,
                                roomPrice = room.price,
                                totalPrice = totalPrice,
                                nights = nights
                            )

                            // Save to local database
                            hotelRepository.saveReservationLocally(localReservation)
                        }

                        _uiState.update {
                            it.copy(
                                reservationSuccess = true,
                                loading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error creating reservation",
                                loading = false
                            )
                        }
                    }
                    else -> {
                        Log.d("HotelDetailViewModel", "Unhandled Resource state: $result")
                        _uiState.update { it.copy(loading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e("HotelDetailViewModel", "Error: ${e.message}")
                _uiState.update {
                    it.copy(
                        errorMessage = "Error: ${e.message}",
                        loading = false
                    )
                }
            }
        }
    }
}