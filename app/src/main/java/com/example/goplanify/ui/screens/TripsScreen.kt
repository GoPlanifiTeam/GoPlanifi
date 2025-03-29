
package com.example.goplanify.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.R
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.SettingsViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripsScreen(
    navController: NavController,
    tripViewModel: TripViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    itineraryViewModel: ItineraryViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = authViewModel.getUserById("test123")
            if (user != null) {
                authViewModel.setCurrentUser(user)
                tripViewModel.getObjectUserTrips(user)
            } else {
                Log.e("ItineraryScreen", "User not found in the database.")
            }
        }
    }
    val currentUser by authViewModel.currentUser.collectAsState()
    val trips by tripViewModel.userTrips.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val lang = settingsViewModel.getSavedLanguageFromRoom(user.userId)
            Log.d("PREFERENCES-LANG", "User ${user.userId} prefers language: $lang")
        }
    }


    if (currentUser == null) {
        Text(text = stringResource(R.string.no_user_logged_in))
        return
    }
    val context = LocalContext.current
    val currentLocale = context.resources.configuration.locales[0]

    LaunchedEffect(Unit) {
        Log.d("LANGUAGE-CHECK", "Current language in TripsScreen: ${currentLocale.language}")
    }


    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.Trips), navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.listExample),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (trips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_trips_found),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(trips) { trip ->
                        TripCard(
                            trip = trip,
                            navController = navController,
                            tripViewModel = tripViewModel,
                            itineraryViewModel = itineraryViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    navController: NavController,
    tripViewModel: TripViewModel,
    itineraryViewModel: ItineraryViewModel
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var newStartDate by remember { mutableStateOf(trip.startDate) }
    var newEndDate by remember { mutableStateOf(trip.endDate) }
    var updatedItineraries by remember { mutableStateOf(trip.itineraries) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    fun stringToDate(dateString: String?): Date? = try { dateFormat.parse(dateString ?: "") } catch (_: Exception) { null }
    fun isDateInFuture(date: String): Boolean = stringToDate(date)?.after(Calendar.getInstance().time) == true

    if (showDateDialog) {
        AlertDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                Button(onClick = {
                    val validStart = isDateInFuture(newStartDate)
                    val validEnd = isDateInFuture(newEndDate)
                    val startDateParsed = stringToDate(newStartDate)
                    val endDateParsed = stringToDate(newEndDate)

                    val itinerariesValid = updatedItineraries.all {
                        val d = stringToDate(it.startDate)
                        d != null && d.after(Calendar.getInstance().time) && d >= startDateParsed && d <= endDateParsed
                    }

                    if (validStart && validEnd && startDateParsed != null && endDateParsed != null && startDateParsed <= endDateParsed && itinerariesValid) {
                        updatedItineraries.forEach { itinerary ->
                            itineraryViewModel.updateItineraryDates(
                                itinerary.id,
                                itinerary.startDate,
                                itinerary.startDate
                            )
                        }

                        val updatedTrip = trip.copy(
                            startDate = newStartDate,
                            endDate = newEndDate,
                            itineraries = updatedItineraries
                        )

                        tripViewModel.updateTrip(updatedTrip)
                        showDateDialog = false
                        navController.popBackStack()
                        navController.navigate("tripsScreen")
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showDateDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text(stringResource(R.string.edit_trip_title)) },
            text = {
                Column {
                    DatePickerInline(newStartDate, { newStartDate = it }, stringResource(R.string.trip_start_date_label))
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerInline(newEndDate, { newEndDate = it }, stringResource(R.string.trip_end_date_label))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.itinerary_label), style = MaterialTheme.typography.titleSmall)
                    updatedItineraries.forEachIndexed { index, itinerary ->
                        var itineraryStart by remember { mutableStateOf(itinerary.startDate) }
                        Text(text = "🏷️ ${itinerary.name} - ${itinerary.location}")
                        DatePickerInline(
                            currentDate = itineraryStart,
                            onDateSelected = {
                                if (isDateInFuture(it) && it >= newStartDate && it <= newEndDate) {
                                    itineraryStart = it
                                    updatedItineraries = updatedItineraries.toMutableList().also {
                                        it[index] = it[index].copy(startDate = itineraryStart, endDate = itineraryStart)
                                    }
                                }
                            },
                            label = "Itinerary Date"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        )
    }

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
            Text("🌍 ${stringResource(R.string.destination)}: ${trip.destination}", style = MaterialTheme.typography.titleMedium)
            Text("📅 ${stringResource(R.string.trip_start_date)}: ${trip.startDate}")
            Text("📅 ${stringResource(R.string.trip_end_date)}: ${trip.endDate}")
            Spacer(modifier = Modifier.height(8.dp))
            trip.itineraries.forEach {
                Text("🏷️ ${it.name} - ${it.location}")
                Text("📅 ${it.startDate}")
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showDateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🛠️ ${stringResource(R.string.edit_dates)}")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        tripViewModel.deleteTrip(trip.id)
                        navController.popBackStack()
                        navController.navigate("tripsScreen")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🗑️ ${stringResource(R.string.delete)}")
                }
            }
        }
    }
}

@Composable
fun DatePickerInline(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    label: String
) {
    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf(currentDate) }
    val datePickerDialog = remember { mutableStateOf<DatePickerDialog?>(null) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    LaunchedEffect(Unit) {
        datePickerDialog.value = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                selectedDate.value = formattedDate
                onDateSelected(formattedDate)
            },
            year, month, day
        )
    }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label)
        Button(onClick = { datePickerDialog.value?.show() }) {
            Text(text = selectedDate.value.ifEmpty { stringResource(R.string.select_date) })
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
            label = { Text(stringResource(R.string.menu)) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = stringResource(R.string.itinerary)) },
            selected = false,
            onClick = { navController.navigate("ItineraryScreen") },
            label = { Text(stringResource(R.string.list)) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.myTrips)) },
            selected = false,
            onClick = { navController.navigate("tripsScreen") },
            label = { Text(stringResource(R.string.profileScreen)) }
        )
    }
}
