package com.example.healthassistant.login

import com.example.healthassistant.consent.ConsentRepository
import com.example.healthassistant.consent.LocalConsentStore
import com.example.healthassistant.core.repositories.LoginRepository
import com.example.healthassistant.core.stores.TokenStore
import javax.inject.Inject

sealed class LoginResult {
    data class ConsentRequired(val consentId: String) : LoginResult()
    object ReadyToUpload : LoginResult()
    object Error : LoginResult()
}

class LoginUseCase @Inject constructor(
    private val loginRepo: LoginRepository,
    private val consentRepo: ConsentRepository,
    private val tokenStore: TokenStore,
    private val localConsentStore: LocalConsentStore
) {
    suspend fun loginAndDecide(
        username: String,
        password: String
    ): LoginResult {
        val token = loginRepo.login(username, password)
        if (token.isNullOrBlank()) return LoginResult.Error

        tokenStore.saveToken(token)

        val availableConsents = consentRepo.getAvailableConsents(token)

        // Cache available consents for offline support
        localConsentStore.saveAvailableConsents(availableConsents)

        return if (availableConsents.isNotEmpty()) {
            LoginResult.ConsentRequired(availableConsents.first().consentId)
        } else {
            LoginResult.ReadyToUpload
        }
    }
}
