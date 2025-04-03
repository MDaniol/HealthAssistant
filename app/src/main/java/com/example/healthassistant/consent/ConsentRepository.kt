package com.example.healthassistant.consent

import com.example.healthassistant.consent.model.Consent
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.headers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

// ConsentRepository.kt
interface ConsentRepository {
    suspend fun getAvailableConsents(token: String): List<Consent>
}

class ConsentRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : ConsentRepository {

    override suspend fun getAvailableConsents(token: String): List<Consent> {
        return try {
            val response = client.get("http://10.0.2.2:8000/api/consents/available") {
                headers { append("Authorization", "Bearer $token") }
            }
            val json = Json.decodeFromString<JsonObject>(response.bodyAsText())
            val consentsJson = json["availableConsents"]?.toString() ?: return emptyList()
            Json.decodeFromString(consentsJson)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}