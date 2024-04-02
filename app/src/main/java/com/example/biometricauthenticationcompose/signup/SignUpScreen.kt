package com.example.biometricauthenticationcompose.signup

import android.app.Activity
import android.content.Intent
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.biometricauthenticationcompose.R
import com.example.biometricauthenticationcompose.common.CustomOutlinedTextField
import com.example.biometricauthenticationcompose.home.HomeActivity
import com.example.biometricauthenticationcompose.utils.BiometricHelper
import com.example.biometricauthenticationcompose.utils.EnableBiometricDialog

@Composable
fun SignUpScreen() {
    val context = LocalContext.current as FragmentActivity
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(context) }
    val viewModel = hiltViewModel<SignUpScreenViewModel>()
    val emailId by viewModel.emailId.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val state by viewModel.state.collectAsState()
    val isPasswordError = state is SignUpState.InvalidPassword
    val isConfirmPasswordError = state is SignUpState.InvalidConfirmPassword
    val isEmailError = state is SignUpState.InvalidEmailId
    var showBiometricEnableDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state) {
        if (state is SignUpState.SUCCESS) {
            showBiometricEnableDialog = true
        }
    }

    if (showBiometricEnableDialog) {
        if (isBiometricAvailable) {
            EnableBiometricDialog(
                onEnable = {
                    BiometricHelper.registerUserBiometrics(context) {
                        showBiometricEnableDialog = false
                        viewModel.setBiometricEnabled(true)
                        context.startHomeActivity()
                    }
                },
                {
                    context.startHomeActivity()
                }
            )
        } else {
            context.startHomeActivity()
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
            text = stringResource(R.string.sign_up_screen_create_account_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomOutlinedTextField(
            label = stringResource(id = R.string.enter_email_id_text),
            text = emailId,
            isPassword = false,
            isError = isEmailError
        ) {
            viewModel.onEmailIdChanged(it)
        }

        Spacer(modifier = Modifier.padding(3.dp))
        CustomOutlinedTextField(
            label = stringResource(id = R.string.enter_password_text),
            text = password,
            isPassword = true,
            isError = isPasswordError
        ) {
            viewModel.onPasswordChanged(it)
        }

        Spacer(modifier = Modifier.padding(3.dp))
        CustomOutlinedTextField(
            label = stringResource(R.string.sign_up_screen_confirm_password_text),
            text = confirmPassword,
            isPassword = true,
            isError = isConfirmPasswordError
        ) {
            viewModel.onConfirmPasswordChanged(it)
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
            onClick = {
                viewModel.onSignUpClicked()
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
                Text(
                    text = stringResource(R.string.sign_up_screen_sign_up_btn_text),
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}

fun Activity.startHomeActivity() {
    val homeIntent = Intent(this, HomeActivity::class.java)
    this.startActivity(homeIntent)
    this.finish()
}