package com.example.healthassistant.consent

import com.example.healthassistant.consent.model.Consent

interface ConsentDatabase {
    val consentQueries: ConsentQueries
}


interface ConsentQueries {
    fun insertConsent(consentId: String, title: String, description: String, document: String, studyId: String, version: String)
    fun selectAll(): List<Consent>
    fun clearAll()
}

// MockConsentDatabase.kt (in-memory mock for development)
class MockConsentDatabase : ConsentDatabase {
    private val inMemoryConsents = mutableListOf<Consent>()

    override val consentQueries: ConsentQueries = object : ConsentQueries {
        override fun insertConsent(
            consentId: String,
            title: String,
            description: String,
            document: String,
            studyId: String,
            version: String
        ) {
            inMemoryConsents.add(Consent(consentId, title, description, document, studyId, version))
        }

        override fun selectAll(): List<Consent> = inMemoryConsents.toList()

        override fun clearAll() {
            inMemoryConsents.clear()
        }
    }
}