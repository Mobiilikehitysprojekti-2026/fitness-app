package com.example.fitnessapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.repository.UserAccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val height: Float? = null,
    val weight: Float? = null
) {
    val bmi: Float?
        get() {
            if (height == null || weight == null || height <= 0f) return null
            val h = height / 100f
            return weight / (h * h)
        }

    val bmiCategory: String?
        get() = bmi?.let {
            when {
                it < 18.5f -> "Underweight"
                it < 25f   -> "Normal"
                it < 30f   -> "Overweight"
                else       -> "Obese"
            }
        }
}

class ProfileViewModel(
    val userAccountRepository: UserAccountRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = userAccountRepository.userAccount
        .map { account -> ProfileUiState(height = account.height, weight = account.weight) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun saveHeight(value: String) {
        value.toFloatOrNull()?.let {
            viewModelScope.launch { userAccountRepository.saveHeight(it) }
        }
    }

    fun saveWeight(value: String) {
        value.toFloatOrNull()?.let {
            viewModelScope.launch { userAccountRepository.saveWeight(it) }
        }
    }
}
