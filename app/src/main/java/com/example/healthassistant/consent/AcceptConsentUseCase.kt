package com.example.healthassistant.consent

import javax.inject.Inject

class AcceptConsentUseCase @Inject constructor(
    private val consentRepository: ConsentRepository
) {
    suspend fun accept(consentId: String, deviceId: String): String {
        return consentRepository.grantConsent(consentId, deviceId)
    }
}