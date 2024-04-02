package com.example.biometricauthenticationcompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biometricauthenticationcompose.signin.SignInScreen
import com.example.biometricauthenticationcompose.signup.SignUpScreen
import com.example.biometricauthenticationcompose.ui.theme.BiometricAuthenticationComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAuthenticationComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "signin", builder = {
                        composable("signin") {
                            SignInScreen(navController)
                        }
                        composable("signup") {
                            SignUpScreen()
                        }
                    })
                }
            }
        }
    }
}