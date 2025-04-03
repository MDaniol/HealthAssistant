package com.example.healthassistant.consent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthassistant.consent.GetConsentUseCase
import com.example.healthassistant.consent.model.Consent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentGrantViewModel @Inject constructor(
    private val getConsentUseCase: GetConsentUseCase
) : ViewModel() {

    private val _consent = MutableStateFlow<Consent?>(null)
    val consent: StateFlow<Consent?> = _consent.asStateFlow()

    fun loadConsent() {
        viewModelScope.launch {
            _consent.value = getConsentUseCase.getCurrentConsent()
        }
    }
}