package com.example.biometricauthenticationcompose.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EnableBiometricDialog(
    onEnable: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { },
        text = {
            Text(text = "Enable biometric authentication to sign in with your fingerprint or face ID.")
        },
        confirmButton = {
            Button(onClick = {
                onEnable()
            }) {
                Text(text = "Enable")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}