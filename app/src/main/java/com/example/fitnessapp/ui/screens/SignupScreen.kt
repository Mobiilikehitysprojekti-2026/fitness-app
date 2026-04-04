package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.example.fitnessapp.data.local.entity.UserAccount
import com.example.fitnessapp.data.model.SignupState
import com.example.fitnessapp.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }


    val signupState = authViewModel.signupState
    val isLoading = signupState is SignupState.Loading

    // navigation to login
    LaunchedEffect(signupState) {
        if (signupState is SignupState.Success) {
            authViewModel.resetSignupState() // reset signupState
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
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

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Height Field (Number restricted)
        OutlinedTextField(
            value = height,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) height = input
            },
            label = { Text("Height (cm)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Weight Field (Number restricted)
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it},
            label = { Text("Weight (kg)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // input check
        val isFormValid = username.isNotBlank() &&
                password.isNotBlank() &&
                name.isNotBlank() &&
                age.isNotBlank() &&
                height.isNotBlank() &&
                weight.isNotBlank()

        Button(
            onClick = {
                val newUserAccount = UserAccount(
                    username = username,
                    password = password,
                    name = name,
                    age = age.toIntOrNull() ?: 0,
                    height = height.toIntOrNull() ?: 0,
                    weight = weight.toIntOrNull() ?: 0
                )

                authViewModel.signUpUser(newUserAccount)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Creating Account..." else "Sign Up")
        }

        if (signupState is SignupState.Error) {
            Text(
                text = signupState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // back to LoginScreen
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account?"
            )
            TextButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text(
                    text = "Login"
                )
            }
        }
    }
}
