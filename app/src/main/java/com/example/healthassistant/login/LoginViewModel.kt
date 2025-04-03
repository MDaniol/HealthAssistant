package com.example.healthassistant.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.core.KtorClientProvider
import com.example.healthassistant.core.SecureStorage
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class LoginViewModel : ViewModel() {
    fun login(username: String, password: String, context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val client = KtorClientProvider.getClient(null) // no token yet

                val response: HttpResponse = client.submitForm(
                    url = "http://10.0.2.2:8000/api/auth/token",
                    formParameters = Parameters.build {
                        append("username", username)
                        append("password", password)
                    }
                )

                if (response.status.isSuccess()) {
                    val body = Json.decodeFromString<JsonObject>(response.bodyAsText())
                    val token = body["access_token"]?.jsonPrimitive?.content
                    if (!token.isNullOrBlank()) {
                        SecureStorage.saveToken(context, token)
                        onResult(true)
                        return@launch
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onResult(false)
        }
    }
}
