package com.example.healthassistant.consent

import com.example.healthassistant.consent.model.Consent
import javax.inject.Inject

class GetConsentUseCase @Inject constructor(
    private val localConsentStore: LocalConsentStore
) {
    suspend fun getCurrentConsent(): Consent? {
        return localConsentStore.getCachedConsents().firstOrNull()
    }
}