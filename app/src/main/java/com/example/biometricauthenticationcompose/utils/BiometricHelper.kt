package com.example.biometricauthenticationcompose.utils

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.biometricauthenticationcompose.manager.CryptoManager
import com.example.biometricauthenticationcompose.signup.startHomeActivity
import java.util.UUID

object BiometricHelper {
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> true
                else -> {
                    Log.e("TAG", "Biometric authentication not available")
                    false
                }
            }
        } else {
            return when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> true
                else -> {
                    Log.e("TAG", "Biometric authentication not available")
                    false
                }
            }
        }
    }

    fun getBiometricPrompt(
        context: FragmentActivity,
        onAuthSucceed: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {
        val biometricPrompt =
            BiometricPrompt(
                context,
                ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        Log.e("TAG", "Authentication Succeeded: ${result.cryptoObject}")
                        onAuthSucceed(result)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.e("TAG", "onAuthenticationError")
                        // TODO Handle authentication errors.
                    }

                    override fun onAuthenticationFailed() {
                        Log.e("TAG", "onAuthenticationFailed")
                        // TODO Handle authentication failures.
                    }
                }
            )
        return biometricPrompt
    }

    fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication App")
            .setSubtitle("Log in using your biometric credential")
            .setDescription("Please authenticate to log in to the app")
            .setConfirmationRequired(false)
            .setNegativeButtonText("Use password instead")
            .build()
    }

    fun registerUserBiometrics(
        context: FragmentActivity,
        onSuccess: (authResult: BiometricPrompt.AuthenticationResult) -> Unit = {}
    ) {
        val cryptoManager = CryptoManager()
        val cypher = cryptoManager.initEncryptionCipher(SECRET_KEY)
        val biometricPrompt =
            getBiometricPrompt(context) { authResult ->
                authResult.cryptoObject?.cipher?.let { cipher ->
                    val token = UUID.randomUUID().toString()
                    val encryptedToken = cryptoManager.encrypt(token, cipher)
                    cryptoManager.saveToPrefs(
                        encryptedToken,
                        context,
                        ENCRYPTED_FILE_NAME,
                        Context.MODE_PRIVATE,
                        PREF_BIOMETRIC
                    )
                    onSuccess(authResult)
                }
            }
        biometricPrompt.authenticate(
            getPromptInfo(),
            BiometricPrompt.CryptoObject(cypher)
        )
    }

    fun authenticateUser(
        context: FragmentActivity,
        onSuccess: (plainText: String) -> Unit
    ) {
        val cryptoManager = CryptoManager()
        val encryptedData = cryptoManager.getFromPrefs(
            context,
            ENCRYPTED_FILE_NAME,
            Context.MODE_PRIVATE,
            PREF_BIOMETRIC
        )
        encryptedData?.let { data ->
            val cypher = cryptoManager.initDecryptionCipher(SECRET_KEY, data.initializationVector)
            val biometricPrompt =
                getBiometricPrompt(context) { authResult ->
                    authResult.cryptoObject?.cipher?.let { cipher ->
                        val plainText = cryptoManager.decrypt(data.ciphertext, cipher)
                        onSuccess(plainText)
                    }
                }
            val promptInfo = getPromptInfo()
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cypher))
        }
    }
}