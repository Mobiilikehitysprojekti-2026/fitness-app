package com.example.fitnessapp.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserAccount(
    val height: Float? = null,
    val weight: Float? = null
)

object UserAccountRepository {

    private val _userAccount = MutableStateFlow(UserAccount())
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    suspend fun saveHeight(value: Float) {
        _userAccount.value = _userAccount.value.copy(height = value)
    }

    suspend fun saveWeight(value: Float) {
        _userAccount.value = _userAccount.value.copy(weight = value)
    }
}
