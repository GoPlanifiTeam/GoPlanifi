package com.example.goplanify.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.goplanify.BuildConfig
import com.example.goplanify.ui.viewmodel.HotelDetailViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotelId: String,
    groupId: String,
    startDateStr: String,
    endDateStr: String,
    navController: NavController,
    viewModel: HotelDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val base = BuildConfig.HOTELS_API_URL.trimEnd('/')
    var showConfirmation by remember { mutableStateOf(false) }
    var showRoomImages by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<String>>(emptyList()) }

    // Calcular noches de estancia
    val dateFormat = DateTimeFormatter.ISO_DATE
    val startDate = LocalDate.parse(startDateStr, dateFormat)
    val endDate = LocalDate.parse(endDateStr, dateFormat)
    val nights = ChronoUnit.DAYS.between(startDate, endDate).toInt()

    // Cargar datos del hotel al entrar
    LaunchedEffect(hotelId) {
        viewModel.loadHotelDetails(hotelId, groupId, startDateStr, endDateStr)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.hotel?.name ?: "Hotel Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Información de fechas
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Stay: $startDateStr → $endDateStr ($nights nights)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Imagen principal del hotel
            item {
                uiState.hotel?.imageUrl?.let { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(base + imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Lista de habitaciones
            items(uiState.rooms) { room ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = room.roomType,
                            fontWeight = FontWeight.Bold
                        )
                        Text("${room.price} € / night")

                        Spacer(Modifier.height(8.dp))

                        // Imagen de la habitación (si hay)
                        room.images.firstOrNull()?.let { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(base + imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clickable {
                                        selectedImages = room.images.map { base + it }
                                        showRoomImages = true
                                    }
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Precio total y botón de reserva
                        val totalPrice = room.price * nights
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total: ${totalPrice}€",
                                fontWeight = FontWeight.Bold
                            )
                            Button(onClick = {
                                viewModel.selectRoom(room)
                                showConfirmation = true
                            }) {
                                Text("Reserve")
                            }
                        }
                    }
                }
            }
        }
    }

    // Carrusel de imágenes integrado directamente
    if (showRoomImages && selectedImages.isNotEmpty()) {
        var currentImageIndex by remember { mutableStateOf(0) }

        Dialog(
            onDismissRequest = { showRoomImages = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
            ) {
                // Imagen actual
                Image(
                    painter = rememberAsyncImagePainter(selectedImages[currentImageIndex]),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Controles de navegación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón anterior
                    if (currentImageIndex > 0) {
                        IconButton(
                            onClick = { currentImageIndex-- },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Previous",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(Modifier.size(48.dp))
                    }

                    // Botón siguiente
                    if (currentImageIndex < selectedImages.size - 1) {
                        IconButton(
                            onClick = { currentImageIndex++ },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(Modifier.size(48.dp))
                    }
                }

                // Botón cerrar
                IconButton(
                    onClick = { showRoomImages = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Diálogo de confirmación de reserva
    if (showConfirmation && uiState.selectedRoom != null) {
        val totalPrice = uiState.selectedRoom!!.price * nights
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Confirm Reservation") },
            text = {
                Column {
                    Text("Hotel: ${uiState.hotel?.name}")
                    Text("Room: ${uiState.selectedRoom!!.roomType}")
                    Text("Dates: $startDateStr to $endDateStr")
                    Text("Nights: $nights")
                    Text("Total: ${totalPrice}€")
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.reserveRoom()
                    showConfirmation = false
                    navController.navigate("reservations") {
                        popUpTo("book") { inclusive = false }
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}