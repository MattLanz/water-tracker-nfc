package com.example.watertracker.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPrefsRepository(private val context: Context) {
    private val DAILY_GOAL_KEY = floatPreferencesKey("daily_goal_liters")

    val dailyGoal: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[DAILY_GOAL_KEY] ?: 2.0f
    }

    suspend fun setDailyGoal(liters: Float) {
        context.dataStore.edit { prefs ->
            prefs[DAILY_GOAL_KEY] = liters
        }
    }
}
