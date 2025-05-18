package com.example.goplanify.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.ReservationsViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun CancelConfirmationDialog(
    reservation: Reservation,
    tripName: String,
    onConfirmCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Warning: Linked Trip") },
        text = {
            Column {
                Text("This reservation is linked to the trip: \"$tripName\".")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cancelling this hotel reservation will also delete the linked trip.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Are you sure you want to continue?", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Reservation & Delete Trip")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Both")
            }
        }
    )
}

@Composable
fun ReservationCard(
    reservation: Reservation,
    linkedTrip: Trip?,
    onCancel: () -> Unit,
    onViewTrip: () -> Unit,
    base: String
) {
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

                    // Show linked trip if available
                    if (linkedTrip != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onViewTrip)
                        ) {
                            Icon(
                                Icons.Default.TravelExplore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Linked to trip: ${linkedTrip.destination}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Botón para cancelar la reserva
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel Reservation"
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationsScreen(
    navController: NavController,
    viewModel: ReservationsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    tripViewModel: TripViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val trips by tripViewModel.trips.collectAsState()
    val base = BuildConfig.HOTELS_API_URL.trimEnd('/')

    var showCancelDialog by remember { mutableStateOf<Pair<Reservation, Trip>?>(null) }

    // Pass the current user to the ViewModels
    LaunchedEffect(currentUser) {
        viewModel.updateCurrentUser(currentUser)
        currentUser?.let { tripViewModel.getObjectUserTrips(it) }
    }

    // Update linked trips when trips change
    LaunchedEffect(trips) {
        viewModel.updateLinkedTrips(trips)
    }

    Scaffold(
        topBar = { CommonTopBar(title = "List", navController) },
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
                        // Find if the reservation is linked to a trip
                        val linkedTrip = trips.find { it.linkedReservationId == reservation.id }

                        ReservationCard(
                            reservation = reservation,
                            linkedTrip = linkedTrip,
                            onCancel = {
                                if (linkedTrip != null) {
                                    // Show confirm dialog for cancellation with linked trip
                                    showCancelDialog = Pair(reservation, linkedTrip)
                                } else {
                                    // Regular cancellation
                                    viewModel.cancelReservation(reservation)
                                }
                            },
                            onViewTrip = {
                                linkedTrip?.let {
                                    navController.navigate("ItineraryScreen?tripId=${it.id}")
                                }
                            },
                            base = base
                        )
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

    // Confirmation dialog for cancellation with linked trip
    showCancelDialog?.let { (reservation, trip) ->
        CancelConfirmationDialog(
            reservation = reservation,
            tripName = trip.destination,
            onConfirmCancel = {
                viewModel.cancelReservationWithLinkedTrip(reservation, tripViewModel)
                showCancelDialog = null
            },
            onDismiss = {
                showCancelDialog = null
            }
        )
    }
}