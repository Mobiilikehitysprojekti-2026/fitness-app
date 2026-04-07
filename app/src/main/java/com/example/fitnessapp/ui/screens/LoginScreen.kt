package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.data.model.LoginState
import com.example.fitnessapp.ui.navigation.ROUTE_HOME
import com.example.fitnessapp.ui.navigation.ROUTE_LOGIN
import com.example.fitnessapp.ui.navigation.ROUTE_SIGNUP
import com.example.fitnessapp.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState = authViewModel.loginState

    Column (
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Login",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { authViewModel.loginUser(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        when (loginState) {
            is LoginState.Loading -> Text("Loading...")
            is LoginState.Error -> Text(loginState.message)
            is LoginState.Success -> {
                LaunchedEffect(Unit) {
                    authViewModel.resetLoginState()
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            }
            else -> {}
        }

        // signup
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account?",
            )
            TextButton(
                onClick = {
                    navController.navigate(ROUTE_SIGNUP)
                },
            ) {
                Text(
                    text = "Sign Up",
                )
            }
        }
    }
}
