package com.example.goplanify.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.goplanify.BuildConfig
import com.example.goplanify.domain.model.Reservation
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.ReservationsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun ReservationsScreen(
    navController: NavController,
    viewModel: ReservationsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val base = BuildConfig.HOTELS_API_URL.trimEnd('/')

    // Pass the current user to the ViewModel
    LaunchedEffect(currentUser) {
        viewModel.updateCurrentUser(currentUser)
    }

    Scaffold(
        topBar = { CommonTopBar(title = "My Reservations", navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Encabezado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "My Reservations",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Botón de recarga
                IconButton(onClick = { viewModel.loadReservations() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload"
                    )
                }
            }

            // User info
            currentUser?.let {
                Text(
                    text = "Reservations for: ${it.email}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Lista de reservas
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.reservations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No reservations found")
                }
            } else {
                LazyColumn {
                    items(uiState.reservations) { reservation ->
                        // Elemento de reserva directamente en LazyColumn
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                // Información del hotel y fechas
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Imagen del hotel (si está disponible)
                                    reservation.hotel.imageUrl?.let { url ->
                                        Image(
                                            painter = rememberAsyncImagePainter(base + url),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .padding(end = 16.dp)
                                        )
                                    }

                                    // Detalles de la reserva
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = reservation.hotel.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("Room: ${reservation.room.roomType}")

                                        // Calcular noches y precio total
                                        val formatter = DateTimeFormatter.ISO_DATE
                                        val startDate = LocalDate.parse(reservation.startDate, formatter)
                                        val endDate = LocalDate.parse(reservation.endDate, formatter)
                                        val nights = ChronoUnit.DAYS.between(startDate, endDate).toInt()
                                        val totalPrice = reservation.room.price * nights

                                        Text("${reservation.startDate} to ${reservation.endDate}")
                                        Text("${nights} nights - Total: ${totalPrice}€")
                                    }

                                    // Botón para cancelar la reserva
                                    IconButton(onClick = { viewModel.cancelReservation(reservation) }) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Cancel Reservation"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Mensaje de error
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}