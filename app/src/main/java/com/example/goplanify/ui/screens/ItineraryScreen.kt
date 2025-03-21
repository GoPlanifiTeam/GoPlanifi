package com.example.goplanify.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    navController: NavController, 
    tripId: String, 
    tripViewModel: TripViewModel = hiltViewModel(),
    itineraryViewModel: ItineraryViewModel = hiltViewModel()
) {
    // Get the trip and itineraries
    val trip = tripViewModel.trips.collectAsState().value.find { it.id == tripId }
    val itineraries = trip?.itineraries ?: emptyList()

    // Observe the selected itineraries from the view model
    val selectedItineraries by itineraryViewModel.selectedItineraries.collectAsState()

    // Variables for the dates
    var itineraryStartDates by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var itineraryEndDates by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var tripStartDate by remember { mutableStateOf(trip?.startDate ?: "") }
    var tripEndDate by remember { mutableStateOf(trip?.endDate ?: "") }

    Scaffold(
        topBar = { CommonTopBar(title = "Itinerary", navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Display trip information
            Text(text = "TripID: $tripId", modifier = Modifier.padding(bottom = 16.dp))
            Text(text = "Trip to: ${trip?.destination}", modifier = Modifier.padding(bottom = 16.dp))

            itineraries.forEach { itinerary ->
                var isChecked by remember { mutableStateOf(selectedItineraries.contains(itinerary)) }

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
                            itineraryViewModel.toggleItinerarySelection(itinerary)  // Toggle selection in the ViewModel
                        }
                    )
                    Text(text = itinerary.name)
                }

                if (isChecked) {
                    // Show date pickers for selected itineraries
                    DatePicker(
                        currentDate = itineraryStartDates[itinerary.id] ?: "",
                        onDateSelected = { date ->
                            itineraryStartDates = itineraryStartDates + (itinerary.id to date)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePicker(
                        currentDate = itineraryEndDates[itinerary.id] ?: "",
                        onDateSelected = { date ->
                            itineraryEndDates = itineraryEndDates + (itinerary.id to date)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show date pickers for the main trip
            Text(text = "Select dates for the main trip")
            DatePicker(
                currentDate = tripStartDate,
                onDateSelected = { selectedDate -> tripStartDate = selectedDate }
            )
            Spacer(modifier = Modifier.height(8.dp))
            DatePicker(
                currentDate = tripEndDate,
                onDateSelected = { selectedDate -> tripEndDate = selectedDate }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validate dates and update itineraries
                    var isValid = true

                    if (tripStartDate >= trip?.startDate.toString() && tripEndDate <= trip?.endDate.toString()) {
                        selectedItineraries.forEach { itinerary ->
                            val startDate = itineraryStartDates[itinerary.id] ?: ""
                            val endDate = itineraryEndDates[itinerary.id] ?: ""

                            if (trip != null) {
                                if (startDate >= trip.startDate && endDate <= trip.endDate) {
                                    itineraryViewModel.updateItineraryDates(itinerary.id, startDate, endDate)
                                } else {
                                    isValid = false
                                }
                            }
                        }
                    } else {
                        isValid = false
                    }

                    if (isValid) {
                        navController.navigate("tripsScreen")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Dates and Go Back")
            }
        }
    }
}

@Composable
fun DatePicker(
    onDateSelected: (String) -> Unit,
    currentDate: String
) {
    val context = LocalContext.current
    val datePickerDialog = remember { mutableStateOf<DatePickerDialog?>(null) }
    val selectedDate = remember { mutableStateOf(currentDate) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Create a DatePickerDialog when the date is selected
    LaunchedEffect(Unit) {
        datePickerDialog.value = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Format selected date as "YYYY-MM-DD"
                selectedDate.value = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                onDateSelected(selectedDate.value) // Pass the selected date to the caller
            },
            year,
            month,
            dayOfMonth
        )
    }

    // Show the date picker dialog
    Button(onClick = { datePickerDialog.value?.show() }) {
        Text(text = if (selectedDate.value.isEmpty()) "Select Date" else selectedDate.value)
    }
}
