package com.example.biometricauthenticationcompose.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.biometricauthenticationcompose.utils.PREF_BIOMETRIC
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

class BiometricPreferences @Inject constructor(
    @Named(PREF_BIOMETRIC) private val preferencesDataStore: DataStore<Preferences>
) {
    object PreferencesKey {
        val KEY_TOKEN = stringPreferencesKey("user_token")
        val KEY_USERNAME = stringPreferencesKey("user_name")
        val KEY_PASSWORD = stringPreferencesKey("user_password")
        val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

    suspend fun setToken(token: String) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_TOKEN] = token
        }
    }

    suspend fun getUserName(): String? {
        return preferencesDataStore.data.first()[PreferencesKey.KEY_USERNAME]
    }

    suspend fun setUserName(userName: String) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_USERNAME] = userName
        }
    }

    suspend fun getPassword(): String? {
        return preferencesDataStore.data.first()[PreferencesKey.KEY_PASSWORD]
    }

    suspend fun setPassword(password: String) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_PASSWORD] = password
        }
    }

    suspend fun isBiometricEnabled(): Boolean {
        return preferencesDataStore.data.first()[PreferencesKey.KEY_BIOMETRIC_ENABLED] ?: false
    }

    suspend fun setBiometricEnabled(isEnabled: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_BIOMETRIC_ENABLED] = isEnabled
        }
    }
}