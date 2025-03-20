package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelplanner.R
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.ui.viewmodel.ItineraryViewModel

@Composable
fun ItineraryScreen(navController: NavController, tripId: String) {
    val itineraryViewModel: ItineraryViewModel = viewModel()

    // Obtener itinerarios para el viaje seleccionado usando tripId
    val itineraries by itineraryViewModel.itineraries.collectAsState()

    LaunchedEffect(tripId) {
        itineraryViewModel.fetchItineraryItems(tripId) // Cargar los itinerarios con tripId
    }

    var selectedItineraries by remember { mutableStateOf<List<ItineraryItem>>(emptyList()) }
    val tripsRoute = stringResource(R.string.trip) // Obtener la ruta de viajes

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.trip),navController) },
        bottomBar = { BottomBar(navController) } // Se agregó el BottomBar aquí
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.trip, tripId),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar la lista de itinerarios
            itineraries.forEach { itinerary ->
                var isChecked by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            if (it) {
                                selectedItineraries = selectedItineraries + itinerary
                            } else {
                                selectedItineraries = selectedItineraries - itinerary
                            }
                        }
                    )
                    Text(text = itinerary.name)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Después de la selección, navegar a la pantalla de viajes
                    navController.navigate(tripsRoute)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.trip))
            }
        }
    }
}
