package com.example.healthassistant.core

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json

object KtorClientProvider {
    fun getClient(token: String?): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                if (!token.isNullOrBlank()) {
                    headers.append("Authorization", "Bearer $token")
                }
            }
        }
    }
}