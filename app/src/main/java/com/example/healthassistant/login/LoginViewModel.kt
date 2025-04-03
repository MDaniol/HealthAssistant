package com.example.healthassistant.login

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
    private val loginRepository: LoginRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _loginStatus = MutableStateFlow(LoginStatus.Idle)
    val loginStatus: StateFlow<LoginStatus> = _loginStatus

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginStatus.value = LoginStatus.Loading
            val token = loginRepository.login(username, password)
            if (!token.isNullOrBlank()) {
                tokenStore.saveToken(token)
                _loginStatus.value = LoginStatus.Success
            } else {
                _loginStatus.value = LoginStatus.Failure
            }
        }
    }
}