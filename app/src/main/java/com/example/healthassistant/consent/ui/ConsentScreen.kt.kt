package com.example.healthassistant.consent.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState


@Composable
fun ConsentScreen(
    consentId: String,
    onConsentAccepted: () -> Unit,
    consentViewModel: ConsentScreenViewModel = hiltViewModel()
) {
    // Ładowanie zgody przy pierwszym wejściu na ekran
    LaunchedEffect(consentId) {
        consentViewModel.loadConsent(consentId)
    }

    // Pobieranie stanu zgody z ViewModel
    val consent = consentViewModel.consent.collectAsState().value


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            consent == null -> {
                CircularProgressIndicator()
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = consent?.title ?: "",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Study ID: ${consent?.studyId ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Version: ${consent?.version ?: ""}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = consent?.description ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onConsentAccepted,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("I Agree and Continue")
                    }
                }
            }
        }
    }
}
