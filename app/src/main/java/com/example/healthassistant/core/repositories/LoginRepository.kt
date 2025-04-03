package com.example.healthassistant.core.repositories

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

interface LoginRepository {
    suspend fun login(username: String, password: String): String? // returns token
}

class LoginRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : LoginRepository {

    override suspend fun login(username: String, password: String): String? {
        return try {
            val response: HttpResponse = client.submitForm(
                url = "http://10.0.2.2:8000/api/auth/token",
                formParameters = Parameters.build {
                    append("username", username)
                    append("password", password)
                }
            )
            if (response.status.isSuccess()) {
                val body = Json.decodeFromString<JsonObject>(response.bodyAsText())
                body["access_token"]?.jsonPrimitive?.content
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}