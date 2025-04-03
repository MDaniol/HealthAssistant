package com.example.healthassistant.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import com.example.healthassistant.core.stores.TokenStore
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.http.encodedPath

object KtorClientProvider {

    fun getClient(tokenStore: TokenStore): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenStore.getToken() ?: ""
                        BearerTokens(token, "")
                    }
                    sendWithoutRequest { request ->
                        // Do NOT send token when requesting /auth/token
                        val path = request.url.encodedPath
                        !path.contains("/api/auth/token")
                    }
                }
            }
        }
    }
}