package com.example.healthassistant.consent.model

import kotlinx.serialization.Serializable


@Serializable
data class AvailableConsents(
    val consents: List<Consent>
)
// LocalConsentStore.kt
@Serializable
data class Consent(
    val consentId: String,
    val title: String,
    val document: String,
    val description: String,
    val studyId: String,
    val version: String
)