package com.example.biometricauthenticationcompose.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.biometricauthenticationcompose.R


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomOutlinedTextField(
    label: String,
    isPassword: Boolean = false,
    text: String,
    isError: Boolean,
    showTrailingIcon: Boolean = false,
    onTrailingIconClicked: () -> Unit = {},
    onValueChange: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = text,
        onValueChange = { onValueChange(it) },
        shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
        label = {
            Text(
                label,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        trailingIcon = {
            if (showTrailingIcon) {
                IconButton(onClick = {
                    onTrailingIconClicked()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.biometric_icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        isError = isError,
        placeholder = { Text(text = label) },
        keyboardOptions =
        KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = if (isPassword) {
                KeyboardType.Password
            } else {
                KeyboardType.Text
            }
        ),
        colors =
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(0.8f),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
    )
}