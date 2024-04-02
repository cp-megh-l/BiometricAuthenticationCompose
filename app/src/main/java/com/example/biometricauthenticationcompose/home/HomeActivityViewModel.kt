package com.example.biometricauthenticationcompose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biometricauthenticationcompose.preferences.BiometricPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val preferences: BiometricPreferences
): ViewModel() {

    val isBiometricEnabled = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            isBiometricEnabled.value = preferences.isBiometricEnabled()
        }
    }

    fun setBiometricEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            preferences.setBiometricEnabled(isEnabled)
            isBiometricEnabled.value = isEnabled
        }
    }
}