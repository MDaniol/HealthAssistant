package com.example.healthassistant.consent

import com.example.healthassistant.consent.model.AvailableConsents
import com.example.healthassistant.consent.model.Consent
import com.example.healthassistant.core.KtorClientProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.headers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonArray


// ConsentRepository.kt
interface ConsentRepository {
    suspend fun getAvailableConsents(token: String): List<Consent>
}

class ConsentRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : ConsentRepository {

    override suspend fun getAvailableConsents(token: String): List<Consent> {
        return try {

            val ktorClient = KtorClientProvider.getClient(token)

            val response = ktorClient.get("http://10.0.2.2:8000/api/consents/available") {
                headers {
                    append("Authorization", "Bearer $token")
                    append("Accept", "application/json")
                }
            }

            val availableConsents = response.body<AvailableConsents>()
            availableConsents.consents
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}