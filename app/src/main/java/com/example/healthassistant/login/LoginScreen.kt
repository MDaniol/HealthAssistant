package com.example.healthassistant.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onConsentRequired: (String) -> Unit,
    onReadyForUpload: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val showError = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(align = Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username.value,
            onValueChange = {
                username.value = it
                showError.value = false
            },
            label = { Text("Username") }
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = password.value,
            onValueChange = {
                password.value = it
                showError.value = false
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))

        if (showError.value) {
            Text("Logowanie nie powiodło się. Sprawdź dane.", color = Color.Red)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                viewModel.login(
                    username = username.value,
                    password = password.value,
                    onConsentRequired = { consentId ->
                        onConsentRequired(consentId)
                    },
                    onUploadReady = onReadyForUpload,
                    onError = { showError.value = true }
                )
            },
            enabled = !viewModel.isLoading
        ) {
            Text("Login")
        }

        if (viewModel.isLoading) {
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator()
        }
    }
}

