package com.example.fitnessapp.managers

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fitnessapp.model.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * This class reads/writes the userId to storage
 */
class UserPreferencesManager(private val context: Context) {
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    // Theme preference
    val isDarkMode: Flow<Boolean?> = context.dataStore.data
        .map { preferences -> preferences[IS_DARK_MODE] }

    suspend fun saveDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    // read
    val userId: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID] } // returns null if not found

    // write
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    // delete
    suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
        }
    }
}