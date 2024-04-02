package com.example.biometricauthenticationcompose.signin

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
class SignInScreenViewModel @Inject constructor(
    private val preferences: BiometricPreferences
) : ViewModel() {

    val emailId = MutableStateFlow("")
    val password = MutableStateFlow("")
    val state = MutableStateFlow<SignInState?>(null)
    val showBiometricPrompt = MutableStateFlow(false)

    fun onEmailIdChanged(emailId: String) {
        this.emailId.value = emailId
    }

    fun onPasswordChanged(password: String) {
        this.password.value = password
    }

    fun onLoginClicked() = viewModelScope.launch {
        if (!validateEmailId()) {
            state.tryEmit(SignInState.InvalidEmailId)
        } else if (!validatePassword()) {
            state.tryEmit(SignInState.InvalidPassword)
        } else {
            validateUserCredentials()
        }
    }

    private fun validateUserCredentials() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val username = preferences.getUserName()
                val password = preferences.getPassword()
                if (emailId.value != username || this@SignInScreenViewModel.password.value != password) {
                    state.tryEmit(SignInState.InvalidCredentials)
                } else {
                    state.tryEmit(SignInState.Success)
                }
            }
        }
    }

    private fun validateEmailId(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.value).matches()
    }

    private fun validatePassword(): Boolean {
        return password.value.length > 5
    }

    fun checkIfBiometricLoginEnabled() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isBiometricEnabled = preferences.isBiometricEnabled()
                if (isBiometricEnabled) {
                    showBiometricPrompt.tryEmit(true)
                }
            }
        }
    }

    fun setToken(plainText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Now that you have obtained the token, you can query the server for additional
                // information. We can call this token as sample token for now because it wasn't actually
                // retrieved from the server. If you obtain it from the server then, it would be a genuine token.
                preferences.setToken(plainText)
            }
        }
    }
}

sealed class SignInState {
    data object InvalidEmailId : SignInState()
    data object InvalidPassword : SignInState()
    data object InvalidCredentials : SignInState()
    data object Success : SignInState()
}