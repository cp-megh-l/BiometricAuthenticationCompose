package com.example.biometricauthenticationcompose.utils

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.biometricauthenticationcompose.R
import com.example.biometricauthenticationcompose.manager.CryptoManager
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

    private fun getBiometricPrompt(
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

    private fun getPromptInfo(context: FragmentActivity): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title_text))
            .setSubtitle(context.getString(R.string.biometric_prompt_subtitle_text))
            .setDescription(context.getString(R.string.biometric_prompt_description_text))
            .setConfirmationRequired(false)
            .setNegativeButtonText(context.getString(R.string.biometric_prompt_use_password_instead_text))
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
            getPromptInfo(context),
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
            val promptInfo = getPromptInfo(context)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cypher))
        }
    }
}