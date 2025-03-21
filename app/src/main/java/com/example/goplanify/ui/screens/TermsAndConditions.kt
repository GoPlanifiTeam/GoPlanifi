package com.example.goplanify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goplanify.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(navController: NavController) {
    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.terms_screen), navController) }, // ✅ Now using `CommonTopBar`
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(R.string.terms_title), style = MaterialTheme.typography.headlineMedium) // "Terms & Conditions"
            Text(text = stringResource(R.string.terms_intro)) // "Please read these terms carefully..."

            Divider(thickness = 1.dp)

            Text(text = stringResource(R.string.terms_usage)) // "Usage Policy"
            Text(text = stringResource(R.string.terms_usage_text)) // "By using this app, you agree..."

            HorizontalDivider(thickness = 1.dp)

            Text(text = stringResource(R.string.terms_privacy)) // "Privacy Policy"
            Text(text = stringResource(R.string.terms_privacy_text)) // "We are committed to protecting your data..."

            HorizontalDivider(thickness = 1.dp)

            Text(text = stringResource(R.string.terms_contact)) // "Contact Information"
            Text(text = stringResource(R.string.terms_contact_text)) // "For any questions, reach out to support@example.com"
        }
    }
}
