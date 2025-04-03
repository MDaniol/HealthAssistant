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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val loginStatus = viewModel.loginStatus.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(align = Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") }
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(16.dp))

        when (loginStatus.value) {
            LoginStatus.Loading -> CircularProgressIndicator()
            LoginStatus.Failure -> Text("Login failed. Check your credentials.", color = androidx.compose.ui.graphics.Color.Red)
            else -> Unit
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(username.value, password.value) },
            enabled = loginStatus.value != LoginStatus.Loading
        ) {
            Text("Login")
        }

        if (loginStatus.value == LoginStatus.Success) {
            onLoginSuccess()
        }
    }
}

