package com.example.healthassistant.core.stores

import android.content.Context
import com.example.healthassistant.core.SecureStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface TokenStore {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?

}

class TokenStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenStore {
    override suspend fun saveToken(token: String) {
        SecureStorage.saveToken(context, token)
    }

    override suspend fun getToken(): String? {
        return SecureStorage.getToken(context) // This is already async
    }
}
