package com.example.goplanify.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.goplanify.BuildConfig
import com.example.goplanify.domain.model.Hotel
import com.example.goplanify.ui.viewmodel.BookViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    navController: NavController,
    viewModel: BookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val base = BuildConfig.HOTELS_API_URL.trimEnd('/')

    Column(Modifier.padding(16.dp)) {
        // Selector de ciudad
        ExposedDropdownMenuBox(
            expanded = uiState.cityMenuExpanded,
            onExpandedChange = { viewModel.toggleCityMenu() }
        ) {
            TextField(
                value = uiState.city,
                onValueChange = {},
                readOnly = true,
                label = { Text("City") },
                leadingIcon = { Icon(Icons.Default.Place, null) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = uiState.cityMenuExpanded,
                onDismissRequest = { viewModel.toggleCityMenu() }
            ) {
                listOf("Barcelona", "Paris", "London").forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = { viewModel.selectCity(city) }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Selector de fecha de inicio
        val context = LocalContext.current
        val formatter = DateTimeFormatter.ISO_DATE

        OutlinedTextField(
            value = uiState.startDate?.format(formatter) ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Check-in") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val now = uiState.startDate ?: LocalDate.now()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            viewModel.setStartDate(LocalDate.of(year, month + 1, day))
                        },
                        now.year, now.monthValue - 1, now.dayOfMonth
                    ).show()
                }
        )

        Spacer(Modifier.height(8.dp))

        // Selector de fecha de fin
        OutlinedTextField(
            value = uiState.endDate?.format(formatter) ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Check-out") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val now = uiState.endDate ?: LocalDate.now().plusDays(1)
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            viewModel.setEndDate(LocalDate.of(year, month + 1, day))
                        },
                        now.year, now.monthValue - 1, now.dayOfMonth
                    ).show()
                }
        )

        Spacer(Modifier.height(16.dp))

        // Botón de búsqueda
        Button(
            onClick = { viewModel.searchHotels() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(Modifier.height(16.dp))

        // Mostrar indicador de carga o resultados
        if (uiState.loading) {  // Nota: 'loading' en lugar de 'isLoading'
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }else {
            // Lista de hoteles
            LazyColumn {
                items(uiState.hotels) { hotel ->
                    // Hotel item directamente dentro del LazyColumn
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate(
                                    "hotel/${hotel.id}/${viewModel.getGroupId()}/${uiState.startDate}/${uiState.endDate}"
                                )
                            }
                    ) {
                        Row(Modifier.height(120.dp)) {
                            // Imagen del hotel
                            Image(
                                painter = rememberAsyncImagePainter(base + hotel.imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(120.dp)
                                    .fillMaxHeight()
                            )

                            // Información del hotel
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = hotel.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(hotel.address)
                                Spacer(modifier = Modifier.weight(1f))

                                // Mostrar precio mínimo si hay habitaciones
                                val minPrice = hotel.rooms
                                    ?.minOfOrNull { it.price }
                                    ?.let { "From ${it}€" } ?: "No rooms available"
                                Text(
                                    text = minPrice,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Mensajes de error
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}