package com.example.biometricauthenticationcompose.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biometricauthenticationcompose.preferences.BiometricPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val preferences: BiometricPreferences
): ViewModel() {

    val state = MutableStateFlow<SignUpState?>(null)
    val emailId = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    fun onEmailIdChanged(emailId: String) {
        this.emailId.value = emailId
    }

    fun onPasswordChanged(password: String) {
        this.password.value = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        this.confirmPassword.value = confirmPassword
    }

    fun onSignUpClicked() {
        if (!validateEmailId()) {
            state.tryEmit(SignUpState.InvalidEmailId)
        } else if (!validatePassword()) {
            state.tryEmit(SignUpState.InvalidPassword)
        } else if (!validateConfirmPassword()) {
            state.tryEmit(SignUpState.InvalidConfirmPassword)
        } else {
            saveUserCredentials()
        }
    }

    private fun saveUserCredentials() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                preferences.setUserName(emailId.value)
                preferences.setPassword(password.value)
                state.tryEmit(SignUpState.SUCCESS)
            }
        }
    }

    private fun validateEmailId(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.value).matches()
    }

    private fun validatePassword(): Boolean {
        return password.value.length > 5
    }

    private fun validateConfirmPassword(): Boolean {
        return password.value == confirmPassword.value
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                preferences.setBiometricEnabled(enabled)
            }
        }
    }
}

sealed class SignUpState {
    data object SUCCESS : SignUpState()
    data object InvalidEmailId : SignUpState()
    data object InvalidPassword : SignUpState()
    data object InvalidConfirmPassword : SignUpState()
}