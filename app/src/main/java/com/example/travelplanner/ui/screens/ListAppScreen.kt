package com.example.travelplanner.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.travelplanner.R
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.User
import com.example.travelplanner.ui.viewmodel.ItineraryViewModel
import com.example.travelplanner.ui.viewmodel.ListExampleViewModel

data class ShoppingItem(val id: Int, var name: String, var quantity: Int, var isEditing: Boolean = false)

@Composable
fun TripListItem(trip: Trip, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Trip to: ${trip.destination}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Itineraries: ${trip.itineraries.size}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Trip")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListApp(
    listViewModel: ListExampleViewModel = viewModel(),
    itineraryViewModel: ItineraryViewModel = viewModel()
) {
    val trips by listViewModel.trips.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var tripName by remember { mutableStateOf("") }
    var tripDestination by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.add_trip))
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(trips) { trip ->
                TripListItem(
                    trip = trip,
                    onDeleteClick = { listViewModel.deleteTrip(trip.id) }
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.add_trip)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tripName,
                        onValueChange = { tripName = it },
                        label = { Text(stringResource(R.string.trip_name)) }
                    )
                    OutlinedTextField(
                        value = tripDestination,
                        onValueChange = { tripDestination = it },
                        label = { Text(stringResource(R.string.trip_destination)) }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (tripName.isNotBlank() && tripDestination.isNotBlank()) {
                        val testUser = User(
                            userId = "1",
                            email = "test@example.com",
                            password = "securePass123",
                            firstName = "John",
                            lastName = "Doe",
                            trips = emptyList(),
                            imageURL = "https://example.com/default-profile.png"
                        )
// ✅ Create a User object
                        listViewModel.addTrip(tripName, tripDestination, testUser) // ✅ Pass User
                        showDialog = false
                        tripName = ""
                        tripDestination = ""
                    }
                }) {
                    Text(stringResource(R.string.add))
                }

            }
        )
    }
}

@Composable
fun ShoppingListItem(item: ShoppingItem, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(BorderStroke(2.dp, Color(0XFF018786)), shape = RoundedCornerShape(20)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "${stringResource(R.string.quantity)}: ${item.quantity}", modifier = Modifier.padding(8.dp))

        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}