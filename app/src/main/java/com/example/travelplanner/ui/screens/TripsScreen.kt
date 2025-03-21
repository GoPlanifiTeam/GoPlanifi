package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelplanner.R
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.ui.viewmodel.ItineraryViewModel
import com.example.travelplanner.ui.viewmodel.TripViewModel

@Composable
fun TripsScreen(navController: NavController) {
    val itineraryViewModel: ItineraryViewModel = viewModel()
    // Get selected itineraries from the ViewModel
    val selectedItineraries by itineraryViewModel.selectedItineraries.collectAsState()

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.trip), navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.trip),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (selectedItineraries.isEmpty()) {
                Text("No itineraries selecteds")
            } else {
                selectedItineraries.forEach { itinerary ->
                    Text("Itinerary: ${itinerary.name}, Location: ${itinerary.location}")
                    Text("Start Date: ${itinerary.startDate}, End Date: ${itinerary.endDate}")
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip, navController: NavController, itineraryRoute: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Trip to ${trip.destination}")
            Text(text = "Start Date: ${trip.startDate}")
            Text(text = "End Date: ${trip.endDate}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Navegar a la pantalla del itinerario para este viaje
                    navController.navigate("$itineraryRoute/${trip.id}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.trip))
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.trip)) },
            selected = false,
            onClick = { navController.navigate("home") },
            label = { Text(stringResource(R.string.trip)) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = stringResource(R.string.trip)) },
            selected = false,
            onClick = { navController.navigate("tripsScreen") },
            label = { Text(stringResource(R.string.trip)) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.itinerary)) },
            selected = false,
            onClick = { navController.navigate("tripsScreen") },
            label = { Text(stringResource(R.string.itinerary)) }
        )
    }
}
