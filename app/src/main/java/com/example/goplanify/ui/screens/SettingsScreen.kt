package com.example.goplanify.ui.screens
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goplanify.ui.viewmodel.SettingsViewModel
import androidx.compose.runtime.getValue
import com.example.goplanify.domain.model.User
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goplanify.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val context = LocalContext.current

    val user = User(
        userId = "1",
        email = "test@example.com",
        password = "1234",
        firstName = "John",
        lastName = "Doe",
        trips = emptyList(),
        imageURL = "https://example.com/image.png"
    )

    LaunchedEffect(Unit) {
        settingsViewModel.loadPreferences(user)
    }

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.profileScreen), navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)

            // ✅ Notifications Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Enable Notifications")
                Switch(
                    checked = settingsState.notificationsEnabled,
                    onCheckedChange = { settingsViewModel.toggleNotifications(user, it) }
                )
            }

            LanguageSelector(context = context) { selectedLanguage ->
                settingsViewModel.changeLanguage(user, selectedLanguage, context)
            }
        }
    }
}

@Composable
fun LanguageSelector(context: Context, onLanguageChange: (String) -> Unit) {
    val languages = listOf("English", "Español", "Français", "Português")
    val languageCodes = listOf("en", "es", "fr", "pt")
    var selectedLanguage by remember { mutableStateOf(getSavedLanguage(context)) }
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = "Select Language:")
        Button(onClick = { expanded = true }) {
            Text(selectedLanguage.uppercase())
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        selectedLanguage = languageCodes[index]
                        onLanguageChange(languageCodes[index])
                        expanded = false
                    }
                )
            }
        }
    }
}

fun getSavedLanguage(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
}

fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("language", languageCode).apply()
}

