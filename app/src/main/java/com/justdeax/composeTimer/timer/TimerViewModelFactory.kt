package com.justdeax.composeTimer.timer
import android.app.AlarmManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimerViewModelFactory(
    private val alarmManager: AlarmManager,
    private val navigator: AlarmSettingsNavigator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(alarmManager, navigator) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
