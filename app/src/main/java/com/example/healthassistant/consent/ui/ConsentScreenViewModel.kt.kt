package com.example.healthassistant.consent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.consent.AcceptConsentUseCase
import com.example.healthassistant.consent.GetConsentUseCase
import com.example.healthassistant.consent.model.Consent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentScreenViewModel @Inject constructor(
    private val getConsentUseCase: GetConsentUseCase,
    private val acceptConsentUseCase: AcceptConsentUseCase
) : ViewModel() {

    private val _consent = MutableStateFlow<Consent?>(null)
    val consent: StateFlow<Consent?> = _consent.asStateFlow()

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result.asStateFlow()

    fun loadConsent(consentId: String) {
        viewModelScope.launch {
            _consent.value = getConsentUseCase.getCurrentConsent()
        }
    }

    fun acceptConsent() {
        val id = consent.value?.consentId
        val deviceId = "DEVICE_1"

        if (id != null) {
            viewModelScope.launch {
                val message = acceptConsentUseCase.accept(id, deviceId)
                _result.value = message
            }
        }
    }
}