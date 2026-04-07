package com.example.fitnessapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.UserAccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val height: Float = 175f,
    val weight: Float = 75f
) {
    val bmi: Float
        get() {
            val h = height / 100f
            return if (h > 0) weight / (h * h) else 0f
        }

    val bmiCategory: String
        get() = when {
            bmi < 18.5f -> "Underweight"
            bmi < 25f   -> "Normal"
            bmi < 30f   -> "Overweight"
            else       -> "Obese"
        }
}

class ProfileViewModel(
    private val userAccountRepository: UserAccountRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = userAccountRepository.currentUserAccount
        .map { user ->
            if (user != null) {
                ProfileUiState(height = user.height.toFloat(), weight = user.weight.toFloat())
            } else {
                ProfileUiState()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun saveHeight(value: String) {
        value.toFloatOrNull()?.let { height ->
            viewModelScope.launch {
                userAccountRepository.updateHeight(height.toInt())
            }
        }
    }

    fun saveWeight(value: String) {
        value.toFloatOrNull()?.let { weight ->
            viewModelScope.launch {
                userAccountRepository.updateWeight(weight.toInt())
            }
        }
    }
}