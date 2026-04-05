package com.example.fitnessapp.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private val HEIGHT_KEY = floatPreferencesKey("height_cm")
    private val WEIGHT_KEY = floatPreferencesKey("weight_kg")


    val heightFlow: Flow<Float> = context.dataStore.data.map { it[HEIGHT_KEY] ?: 175f }
    val weightFlow: Flow<Float> = context.dataStore.data.map { it[WEIGHT_KEY] ?: 75f }

    suspend fun saveHeight(value: Float) {
        context.dataStore.edit { it[HEIGHT_KEY] = value }
    }

    suspend fun saveWeight(value: Float) {
        context.dataStore.edit { it[WEIGHT_KEY] = value }
    }
}
