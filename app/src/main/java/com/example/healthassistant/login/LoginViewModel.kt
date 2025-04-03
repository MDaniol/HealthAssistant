package com.example.healthassistant.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.core.repositories.LoginRepository
import com.example.healthassistant.core.stores.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LoginStatus {
    Idle,
    Loading,
    Success,
    Failure
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    fun login(
        username: String,
        password: String,
        onConsentRequired: (String) -> Unit,
        onUploadReady: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            when (val result = loginUseCase.loginAndDecide(username, password)) {
                is LoginResult.ConsentRequired -> onConsentRequired(result.consentId)
                is LoginResult.ReadyToUpload -> onUploadReady()
                is LoginResult.Error -> onError()
            }
            isLoading = false
        }
    }
}