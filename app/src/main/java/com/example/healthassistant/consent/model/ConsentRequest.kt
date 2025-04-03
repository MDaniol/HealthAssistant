package com.example.healthassistant.consent.model

data class ConsentRequest(
    val userId: String,
    val consentId: String,
    val deviceId: String
)