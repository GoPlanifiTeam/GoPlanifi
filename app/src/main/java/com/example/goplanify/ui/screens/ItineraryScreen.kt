import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.ui.viewmodel.ItineraryViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import com.example.goplanify.R
import com.example.goplanify.domain.model.ItineraryItem
import com.example.goplanify.domain.model.Trip
import com.example.goplanify.ui.screens.BottomBar
import com.example.goplanify.ui.screens.CommonTopBar
import com.example.goplanify.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*





@Composable
fun DatePicker(
    onDateSelected: (String) -> Unit,
    currentDate: String,
    tripStartDate: String,
    tripEndDate: String
) {
    val context = LocalContext.current
    val datePickerDialog = remember { mutableStateOf<DatePickerDialog?>(null) }
    val selectedDate = remember { mutableStateOf(currentDate) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)


    LaunchedEffect(Unit) {
        datePickerDialog.value = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedFormattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                selectedDate.value = selectedFormattedDate
                onDateSelected(selectedFormattedDate)
            },
            year,
            month,
            dayOfMonth
        )
    }

    Button(onClick = { datePickerDialog.value?.show() }) {
        Text(text = selectedDate.value.ifEmpty { stringResource(R.string.add) })
    }
}
@SuppressLint("SimpleDateFormat")
@Composable
fun ItineraryScreen(
    navController: NavController,
    tripId: String? = null,
    tripViewModel: TripViewModel = hiltViewModel(),
    itineraryViewModel: ItineraryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val trips by tripViewModel.userTrips.collectAsState()
    val allTrips by tripViewModel.trips.collectAsState()
    val selectedItineraries by itineraryViewModel.selectedItineraries.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            tripViewModel.getObjectUserTrips(user)
        }
    }

    // If no user is authenticated, show login prompt
    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.no_user_logged_in))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("loginScreen") }) {
                    Text(text = "Login")
                }
            }
        }
        return
    }

    val trip = allTrips.find { it.id == tripId }
    Log.d("User Detected","$currentUser")
    val itineraries: List<ItineraryItem> = when {
        tripId != null && trip != null -> {
            Log.d("ItineraryScreen", "Showing itineraries for tripId $tripId: ${trip.itineraries}")
            trip.itineraries
        }
        currentUser != null -> {
            val userTrips = trips.filter { it.user?.userId == currentUser!!.userId }
            Log.d("ItineraryScreen", "User ${currentUser!!.userId} has ${userTrips.size} trips")
            userTrips.flatMap {
                Log.d("ItineraryScreen", "Trip ${it.id} itineraries: ${it.itineraries}")
                it.itineraries
            }
        }
        else -> {

            Log.d("ItineraryScreen", "No user or trip found.")
            emptyList()
        }
    }

    var itineraryDates by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var tripStartDate by remember { mutableStateOf(trip?.startDate ?: "") }
    var tripEndDate by remember { mutableStateOf(trip?.endDate ?: "") }
    var itineraryDatesValid by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var isTripStartDateValid by remember { mutableStateOf(true) }
    var isTripEndDateValid by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    fun stringToDate(dateString: String?): Date? = try { dateFormat.parse(dateString ?: "") } catch (_: Exception) { null }
    fun isDateInFuture(date: String): Boolean = stringToDate(date)?.after(Calendar.getInstance().time) == true
    fun isDateInTripRange(date: String, startDate: String, endDate: String): Boolean {
        val dateParsed = stringToDate(date)
        val start = stringToDate(startDate)
        val end = stringToDate(endDate)
        return if (dateParsed != null && start != null && end != null) {
            !dateParsed.before(start) && !dateParsed.after(end)
        } else {
            false
        }
    }
    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.itinerary), navController) },
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (tripId != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            trip?.let {
                                Text(text = "✈️ Trip to ${it.destination}", style = MaterialTheme.typography.titleLarge)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "🆔 Trip ID: $tripId", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(
                        text = "📍 Choose your itinerary items:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                } else {
                    Text(text = "Itineraries", modifier = Modifier.padding(bottom = 16.dp))
                }
            }

            if (tripId == null) {
                if (itineraries.isEmpty()) {

                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.no_trips_found), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    val itinerariesByTrip = trips.filter { it.itineraries.isNotEmpty() }.associateWith { it.itineraries }
                    itinerariesByTrip.forEach { (trip, tripItineraries) ->
                        items(tripItineraries) { itinerary ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("🏷️ ${itinerary.name}", style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📍 Location: ${itinerary.location}", style = MaterialTheme.typography.bodyMedium)
                                    Text("📅 Date: ${itinerary.startDate}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    return@LazyColumn
                }
            }

            items(itineraries) { itinerary ->
                var isChecked by remember { mutableStateOf(selectedItineraries.contains(itinerary)) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    isChecked = it
                                    itineraryViewModel.toggleItinerarySelection(itinerary)
                                }
                            )
                            Text(itinerary.name, style = MaterialTheme.typography.titleSmall)
                        }

                        if (isChecked) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("📅 Select date for this itinerary", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            DatePicker(
                                currentDate = itineraryDates[itinerary.id] ?: "",
                                onDateSelected = { date ->
                                    if (isDateInFuture(date)) {
                                        itineraryDates = itineraryDates + (itinerary.id to date)
                                        itineraryDatesValid = itineraryDatesValid + (itinerary.id to true)
                                    } else {
                                        itineraryDatesValid = itineraryDatesValid + (itinerary.id to false)
                                    }
                                },
                                tripStartDate = tripStartDate,
                                tripEndDate = tripEndDate
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛫 Start Date", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        DatePicker(
                            currentDate = tripStartDate,
                            onDateSelected = {
                                tripStartDate = it
                                isTripStartDateValid = isDateInFuture(it)
                            },
                            tripStartDate = tripStartDate,
                            tripEndDate = tripEndDate
                        )
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛬 End Date", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        DatePicker(
                            currentDate = tripEndDate,
                            onDateSelected = {
                                tripEndDate = it
                                isTripEndDateValid = isDateInFuture(it)
                            },
                            tripStartDate = tripStartDate,
                            tripEndDate = tripEndDate
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        var isValid = true
                        val tripStart = stringToDate(tripStartDate)
                        val tripEnd = stringToDate(tripEndDate)

                        if (tripStart != null && tripEnd != null && tripStart <= tripEnd && isTripStartDateValid && isTripEndDateValid) {
                            selectedItineraries.forEach { itinerary ->
                                val date = itineraryDates[itinerary.id] ?: ""
                                val isValidDate = itineraryDatesValid[itinerary.id] == true &&
                                        isDateInFuture(date) &&
                                        isDateInTripRange(date, tripStartDate, tripEndDate)

                                if (isValidDate) {
                                    itineraryViewModel.updateItineraryDates(itinerary.id, date, date)
                                } else {
                                    isValid = false
                                    Log.e("ItineraryScreen", "Itinerary ${itinerary.name} has an invalid date.")
                                }
                            }
                        }


                        if (isValid) {
                            val newTripId = UUID.randomUUID().toString()
                            val newTrip = Trip(
                                map = null,
                                id = newTripId,
                                destination = trip?.destination ?: "User Trip",
                                user = currentUser,
                                startDate = tripStartDate,
                                endDate = tripEndDate,
                                itineraries = selectedItineraries.map { itinerary ->
                                    ItineraryItem(
                                        id = UUID.randomUUID().toString(),
                                        name = itinerary.name,
                                        location = itinerary.location,
                                        startDate = itineraryDates[itinerary.id] ?: "",
                                        endDate = itineraryDates[itinerary.id] ?: "",
                                        trip = newTripId // ✅ correct trip reference
                                    )
                                },
                                images = null,
                                aiRecommendations = null
                            )

                            tripViewModel.addTrip(newTrip)
                            navController.navigate("tripsScreen")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }



}
