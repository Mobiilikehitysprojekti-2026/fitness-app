package com.example.fitnessapp.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserPreferencesRepository(application)

    val uiState: StateFlow<ProfileUiState> = combine(
        repo.heightFlow,
        repo.weightFlow
    ) { height, weight ->
        ProfileUiState(height = height, weight = weight)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun saveHeight(value: String) {
        value.toFloatOrNull()?.let {
            viewModelScope.launch { repo.saveHeight(it) }
        }
    }

    fun saveWeight(value: String) {
        value.toFloatOrNull()?.let {
            viewModelScope.launch { repo.saveWeight(it) }
        }
    }
}