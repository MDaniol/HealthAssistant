package com.example.healthassistant.consent.model

import kotlinx.serialization.Serializable

// LocalConsentStore.kt
@Serializable
data class Consent(
    val consentId: String,
    val title: String,
    val description: String,
    val studyId: String,
    val version: String
)