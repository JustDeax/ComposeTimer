package com.justdeax.composeTimer.timer
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimerViewModelFactory2(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel2::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel2(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
