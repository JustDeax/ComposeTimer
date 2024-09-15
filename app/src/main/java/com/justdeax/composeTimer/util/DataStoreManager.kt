package com.justdeax.composeTimer.util
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("preference")

class DataStoreManager(private val context: Context) {
    companion object { //CT => COUNTDOWN TIMER
        private val CT_REMAINING_TIME = longPreferencesKey("CT_REMAINING_TIME")
        private val CT_IS_RUNNING = booleanPreferencesKey("ST_IS_RUNNING")
        private val CT_FOREGROUND_ENABLED = booleanPreferencesKey("ST_FOREGROUND_ENABLED")
        private val CT_TAP_ON_CLOCK = intPreferencesKey("ST_TAP_ON_CLOCK")
        private val APP_THEME = intPreferencesKey("APP_THEME_CODE")
    }

    suspend fun changeTheme(themeCode: Int) {
        context.dataStore.edit { set -> set[APP_THEME] = themeCode }
    }

    fun getTheme() = context.dataStore.data.map { get ->
        get[APP_THEME] ?: 0
    }

    suspend fun changeTapOnClock(tapType: Int) {
        context.dataStore.edit { set -> set[CT_TAP_ON_CLOCK] = tapType }
    }

    fun getTapOnClock() = context.dataStore.data.map { get ->
        get[CT_TAP_ON_CLOCK] ?: 1
    }

    suspend fun changeForegroundEnabled(enabled: Boolean) {
        context.dataStore.edit { set -> set[CT_FOREGROUND_ENABLED] = enabled }
    }

    fun foregroundEnabled() = context.dataStore.data.map { get ->
        get[CT_FOREGROUND_ENABLED] ?: false //TODO change to TRUE
    }

    suspend fun saveTimer(timerState: TimerState) {
        context.dataStore.edit { set ->
            set[CT_REMAINING_TIME] = timerState.remainingTime
            set[CT_IS_RUNNING] = timerState.isRunning
        }
    }

    fun restoreTimer() = context.dataStore.data.map { get ->
        TimerState(
            get[CT_REMAINING_TIME] ?: 0L,
            get[CT_IS_RUNNING] ?: false
        )
    }

    suspend fun resetStopwatch() {
        context.dataStore.edit { set ->
            set.remove(CT_REMAINING_TIME)
            set.remove(CT_IS_RUNNING)
        }
    }
}