package com.justdeax.composeTimer.timer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.justdeax.composeTimer.util.DataStoreManager

class TimerViewModelFactory(
    private val dataStoreManager: DataStoreManager,
    private val navigator: AlarmSettingsNavigator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(dataStoreManager, navigator) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}