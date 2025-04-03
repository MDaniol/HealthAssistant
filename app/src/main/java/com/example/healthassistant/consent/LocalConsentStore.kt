package com.example.healthassistant.consent

import com.example.healthassistant.consent.model.Consent
import javax.inject.Inject

interface LocalConsentStore {
    suspend fun saveAvailableConsents(consents: List<Consent>)
    suspend fun getCachedConsents(): List<Consent>
}

class LocalConsentStoreImpl @Inject constructor(
    private val database: ConsentDatabase
) : LocalConsentStore {

    override suspend fun saveAvailableConsents(consents: List<Consent>) {
        database.consentQueries.clearAll()
        consents.forEach {
            database.consentQueries.insertConsent(
                it.consentId,
                it.title,
                it.description,
                it.studyId,
                it.version
            )
        }
    }

    override suspend fun getCachedConsents(): List<Consent> {
        return database.consentQueries.selectAll().map {
            Consent(
                it.consentId,
                it.title,
                it.description,
                it.studyId,
                it.version
            )
        }
    }
}
