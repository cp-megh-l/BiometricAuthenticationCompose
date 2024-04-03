---

How to Implement Biometric Authentication with Jetpack Compose and AES Encryption

Exciting News! Our blog has a new home! 🚀
Background
With the increasing reliance on smartphones for various activities, securing access to sensitive information has become paramount. Traditional methods like passwords or PINs are often cumbersome and prone to security breaches. 
Biometric authentication addresses these concerns by leveraging unique biological traits such as fingerprints, facial features, or iris patterns for identity verification. Android devices have built-in support for biometric authentication, making it accessible for developers to integrate into their applications seamlessly.

---

Introduction
Biometric authentication has become a cornerstone of security in modern mobile applications, offering users a convenient and secure way to access sensitive information. In this blog post, we will explore how to implement biometric authentication using Jetpack Compose, the modern UI toolkit for Android, coupled with AES encryption for added security.
Benefits
Biometric authentication offers several advantages over traditional methods like passwords or PINs. Firstly, it provides a seamless user experience, eliminating the need for users to remember complex passwords. Secondly, it enhances security by utilizing unique biological traits such as fingerprints or facial features. Lastly, biometric authentication reduces the risk of unauthorized access, as these biological traits are inherently difficult to replicate.
Types of Authentication Supported
When implementing biometric authentication in an app, it's essential to support various biometric modalities to cater to different devices and user preferences. Android provides support for the following biometric authentication types:
Fingerprint Authentication: Utilizes the unique patterns of a user's fingerprints for authentication.
Face Authentication: Verifies the user's identity by analyzing facial features captured by the device's camera.
Iris Authentication: Scans the unique patterns in the user's iris for authentication.

By supporting multiple authentication types, developers can ensure compatibility with a wide range of devices and accommodate users with various preferences and accessibility needs.
In this blog post, we will focus on implementing fingerprint authentication using Jetpack Compose and AES encryption. 
We will walk through the process of integrating biometric authentication into an Android application and securing sensitive data using AES encryption.

---

Why Cryptographic Solution is Necessary
While biometric authentication offers enhanced security, it's crucial to augment it with cryptographic solutions like AES encryption for robust protection of sensitive data. Here's why cryptographic solutions are essential when working with biometric authentication:
Data Protection: Biometric templates (e.g., fingerprints or facial features) are sensitive data that must be securely stored and transmitted. AES encryption ensures that this data is encrypted both at rest and in transit, minimizing the risk of unauthorized access.
Key Management: AES encryption requires encryption keys to encrypt and decrypt data. By implementing proper key management practices, developers can ensure that these keys are securely managed and protected from unauthorized access or misuse.
Compliance Requirements: Many industries, such as finance and healthcare, are subject to strict regulatory requirements regarding data security and privacy. Implementing AES encryption helps organizations comply with these regulations by safeguarding sensitive user information.
Defense against Attacks: Cryptographic solutions like AES encryption provide an additional layer of defense against various security threats, including data breaches, man-in-the-middle attacks, and unauthorized access attempts.

---

Implementing Biometric Authentication with Jetpack Compose

Step 1: Add Biometric Authentication Dependencies

To get started with biometric authentication in your Android application, you need to add the following dependencies to your app-level build.gradle file:

```
dependencies {
    implementation "androidx.biometric:biometric:1.2.0"
}
```

These dependencies provide the necessary APIs to interact with the biometric hardware on Android devices and authenticate users using their biometric data.

Step 2: Create CryptoManager for AES Encryption that will manage the encryption and decryption of sensitive data using AES encryption. The CryptoManager class will handle key generation, encryption, and decryption operations.

```
package com.example.biometricauthenticationcompose.manager

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.gson.Gson
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// Interface defining cryptographic operations
interface CryptoManager {

    // Initialize encryption cipher
    fun initEncryptionCipher(keyName: String): Cipher

    // Initialize decryption cipher
    fun initDecryptionCipher(keyName: String, initializationVector: ByteArray): Cipher

    // Encrypt plaintext
    fun encrypt(plaintext: String, cipher: Cipher): EncryptedData

    // Decrypt ciphertext
    fun decrypt(ciphertext: ByteArray, cipher: Cipher): String

    // Save encrypted data to SharedPreferences
    fun saveToPrefs(
        encryptedData: EncryptedData,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    )

    // Retrieve encrypted data from SharedPreferences
    fun getFromPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): EncryptedData?
}

// Factory function to create CryptoManager instance
fun CryptoManager(): CryptoManager = CryptoManagerImpl()

// Implementation of CryptoManager interface
class CryptoManagerImpl : CryptoManager {

    // Encryption transformation algorithm
    private val ENCRYPTION_TRANSFORMATION = "AES/GCM/NoPadding"
    // Android KeyStore provider
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    // Key alias for the secret key
    private val KEY_ALIAS = "MyKeyAlias"

    // KeyStore instance
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)

    init {
        // Load the KeyStore
        keyStore.load(null)
        // If key alias doesn't exist, create a new secret key
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createSecretKey()
        }
    }

    // Initialize encryption cipher
    override fun initEncryptionCipher(keyName: String): Cipher {
        val cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        return cipher
    }

    // Initialize decryption cipher
    override fun initDecryptionCipher(keyName: String, initializationVector: ByteArray): Cipher {
        val cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION)
        val spec = GCMParameterSpec(128, initializationVector)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        return cipher
    }

    // Encrypt plaintext
    override fun encrypt(plaintext: String, cipher: Cipher): EncryptedData {
        val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return EncryptedData(encryptedBytes, cipher.iv)
    }

    // Decrypt ciphertext
    override fun decrypt(ciphertext: ByteArray, cipher: Cipher): String {
        val decryptedBytes = cipher.doFinal(ciphertext)
        return String(decryptedBytes, Charset.forName("UTF-8"))
    }

    // Save encrypted data to SharedPreferences
    override fun saveToPrefs(
        encryptedData: EncryptedData,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ) {
        val json = Gson().toJson(encryptedData)
        with(context.getSharedPreferences(filename, mode).edit()) {
            putString(prefKey, json)
            apply()
        }
    }

    // Retrieve encrypted data from SharedPreferences
    override fun getFromPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): EncryptedData? {
        val json = context.getSharedPreferences(filename, mode).getString(prefKey, null)
        return Gson().fromJson(json, EncryptedData::class.java)
    }

    // Create a new secret key
    private fun createSecretKey() {
        val keyGenParams = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setUserAuthenticationRequired(true)
        }.build()

        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParams)
        keyGenerator.generateKey()
    }

    // Retrieve the secret key from KeyStore
    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
}

// Data class to hold encrypted data
data class EncryptedData(val ciphertext: ByteArray, val initializationVector: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedData

        if (!ciphertext.contentEquals(other.ciphertext)) return false
        return initializationVector.contentEquals(other.initializationVector)
    }

    override fun hashCode(): Int {
        var result = ciphertext.contentHashCode()
        result = 31 * result + initializationVector.contentHashCode()
        return result
    }
}
```

Step 3: Create BiomatricHelper that will handle biometric authentication operations

BiometricHelper is a versatile utility object designed to simplify the integration of biometric authentication features into Android applications. This helper class encapsulates complex biometric API interactions, providing developers with a clean and intuitive interface to perform common biometric authentication tasks.

```kotlin

object BiometricHelper {
    
}
```


