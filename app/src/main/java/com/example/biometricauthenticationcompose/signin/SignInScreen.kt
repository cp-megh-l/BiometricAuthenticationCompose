package com.example.biometricauthenticationcompose.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.biometricauthenticationcompose.R
import com.example.biometricauthenticationcompose.common.CustomOutlinedTextField
import com.example.biometricauthenticationcompose.home.HomeActivity
import com.example.biometricauthenticationcompose.manager.CryptoManager
import com.example.biometricauthenticationcompose.utils.BiometricHelper
import com.example.biometricauthenticationcompose.utils.ENCRYPTED_FILE_NAME
import com.example.biometricauthenticationcompose.utils.NavigationRoutes
import com.example.biometricauthenticationcompose.utils.PREF_BIOMETRIC

@Composable
fun SignInScreen(navController: NavHostController) {

    val context = LocalContext.current as FragmentActivity
    val viewModel = hiltViewModel<SignInScreenViewModel>()
    val emailId by viewModel.emailId.collectAsState()
    val password by viewModel.password.collectAsState()

    val state by viewModel.state.collectAsState()

    val isPasswordError = state is SignInState.InvalidPassword
    val isEmailError = state is SignInState.InvalidEmailId
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(context) }
    val showBiometricPrompt by viewModel.showBiometricPrompt.collectAsState()
    val showBiometricIcon = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state) {
        if (state is SignInState.Success) {
            context.navigateToHomeActivity()
        }
    }

    LaunchedEffect(key1 = isBiometricAvailable) {
        if (isBiometricAvailable) {
            viewModel.checkIfBiometricLoginEnabled()
        }
    }

    LaunchedEffect(key1 = showBiometricPrompt) {
        if (showBiometricPrompt) {
            BiometricHelper.authenticateUser(context,
                onSuccess = { plainText ->
                    viewModel.setToken(plainText)
                    context.navigateToHomeActivity()
                })
        } else {
            val cryptoManager = CryptoManager()
            val encryptedData = cryptoManager.getFromPrefs(
                context,
                ENCRYPTED_FILE_NAME,
                Context.MODE_PRIVATE,
                PREF_BIOMETRIC
            )
            encryptedData?.let {
                showBiometricIcon.value = true
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.sign_in_screen_title_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomOutlinedTextField(
            label = stringResource(R.string.enter_email_id_text),
            text = emailId,
            isPassword = false,
            isError = isEmailError,
            showTrailingIcon = showBiometricIcon.value,
            onValueChange = {
                viewModel.onEmailIdChanged(it)
            },
            onTrailingIconClicked = {
                BiometricHelper.authenticateUser(context) { plainText ->
                    viewModel.setToken(plainText)
                    context.navigateToHomeActivity()
                }
            }
        )

        Spacer(modifier = Modifier.padding(3.dp))
        CustomOutlinedTextField(
            label = stringResource(R.string.enter_password_text),
            text = password,
            isPassword = true,
            isError = isPasswordError
        ) {
            viewModel.onPasswordChanged(it)
        }

        Spacer(modifier = Modifier.padding(10.dp))

        AnimatedVisibility(visible = state is SignInState.InvalidCredentials) {
            Text(
                text = stringResource(R.string.invalid_email_or_password_text),
                fontSize = 14.sp,
                color = Color.Red
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
            onClick = {
                viewModel.onLoginClicked()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush =
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF484BF1),
                                Color(0xFFB4A0F5)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Login", fontSize = 20.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))
        TextButton(onClick = { navController.navigate(NavigationRoutes.SIGN_UP) }) {
            Text(
                text = stringResource(R.string.sign_in_screen_create_account_text),
                letterSpacing = 1.sp,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

fun Activity.navigateToHomeActivity() {
    val intent = Intent(this, HomeActivity::class.java)
    startActivity(intent)
    finish()
}