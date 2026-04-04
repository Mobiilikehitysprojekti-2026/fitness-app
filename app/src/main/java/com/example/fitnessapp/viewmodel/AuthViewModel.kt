package com.example.fitnessapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.UserAccount
import com.example.fitnessapp.data.model.LoginState
import com.example.fitnessapp.data.model.SignupState
import com.example.fitnessapp.data.repository.UserAccountRepository
import kotlinx.coroutines.launch

/**
 * AuthViewModel controls authentication (login and signup)
 */
class AuthViewModel(
    private val userAccountRepository: UserAccountRepository
) : ViewModel() {

    // login state
    var loginState by mutableStateOf<LoginState>(LoginState.Idle)
        private set
    var signupState by mutableStateOf<SignupState>(SignupState.Idle)
        private set


    // sign up a new user
    fun signUpUser(newUserAccount: UserAccount) {
        viewModelScope.launch {
            signupState = SignupState.Loading
            try {
                userAccountRepository.insertUserAccount(newUserAccount)
                signupState = SignupState.Success
            } catch (e: Exception) {
                signupState = SignupState.Error(e.message ?: "Failed to create user account")
            }
        }
    }

    // login
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            loginState = LoginState.Loading

            val successfulLogin = userAccountRepository.login(username, password)

            loginState = if (successfulLogin) {
                LoginState.Success
            } else {
                LoginState.Error("Invalid username or password")
            }
        }
    }

    fun resetSignupState() {
        signupState = SignupState.Idle
    }

    fun resetLoginState() {
        loginState = LoginState.Idle
    }
}