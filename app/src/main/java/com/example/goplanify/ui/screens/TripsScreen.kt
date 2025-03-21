package com.example.goplanify.ui.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import com.example.goplanify.R


@Composable
fun TripsScreen(
    navController: NavController,
    itineraryViewModel: ItineraryViewModel = hiltViewModel(),
    tripViewModel: TripViewModel = hiltViewModel()
) {
    // Get selected itineraries from the ViewModel
    val selectedItineraries by itineraryViewModel.selectedItineraries.collectAsState()
    val trips by tripViewModel.trips.collectAsState()
    
    LaunchedEffect(Unit) {
        tripViewModel.fetchTrips()
    }

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
                Text("No itineraries selected")
            } else {
                selectedItineraries.forEach { itinerary ->
                    Text("Itinerary: ${itinerary.name}, Location: ${itinerary.location}")
                    Text("Start Date: ${itinerary.startDate}, End Date: ${itinerary.endDate}")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Your Trips",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (trips.isEmpty()) {
                Text("No trips available")
            } else {
                trips.forEach { trip ->
                    TripCard(trip = trip, navController = navController, itineraryRoute = "ItineraryScreen")
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
