package com.justdeax.composeTimer.timer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.justdeax.composeTimer.util.DataStoreManager
import com.justdeax.composeTimer.util.TimerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(
    private val dataStoreManager: DataStoreManager,
    private val navigator: AlarmSettingsNavigator
) : ViewModel() {
    private var startTime = 0L
    val theme = dataStoreManager.getTheme().asLiveData()
    val tapOnClock = dataStoreManager.getTapOnClock().asLiveData()
    val foregroundEnabled = dataStoreManager.foregroundEnabled().asLiveData()

//    fun changeTheme(themeCode: Int) {
//        viewModelScope.launch {
//            dataStoreManager.changeTheme(themeCode)
//        }
//    }
//
//    fun changeTapOnClock(tapType: Int) {
//        viewModelScope.launch {
//            dataStoreManager.changeTapOnClock(tapType)
//        }
//    }
//
//    fun changeForegroundEnabled(enabled: Boolean) {
//        viewModelScope.launch {
//            dataStoreManager.changeForegroundEnabled(enabled)
//        }
//    }

    fun restoreTimer() {
        viewModelScope.launch {
            dataStoreManager.restoreTimer().collect { restoredState ->
                remainingTime.value = restoredState.timerDuration
                isStarted.value = restoredState.timerDuration != 0L
                isRunning.value = restoredState.isRunning
                if (isRunning.value!!) {
                    startTime = restoredState.startTime
                    startResume(remainingTime.value!!)
                }
            }
        }
    }

    fun startResume(timerDuration: Long) {
        isStarted.value = true
        isRunning.value = true
        navigator.setAlarm(timerDuration)
        viewModelScope.launch(Dispatchers.IO) {
            if (startTime == 0L) startTime = System.currentTimeMillis()
            dataStoreManager.saveTimer(
                TimerState(timerDuration, startTime, isRunning.value!!)
            )
            while (isRunning.value!!) {
                val deltaTime = System.currentTimeMillis() - startTime
                if (deltaTime >= timerDuration)
                    reset()
                else
                    remainingTime.postValue(timerDuration - deltaTime)
                delay(100L)
            }
        }
    }

    fun pause() {
        isRunning.value = false
        navigator.removeAlarm()
        startTime = 0L
        viewModelScope.launch {
            dataStoreManager.saveTimer(
                TimerState(remainingTime.value!!, 0L, false)
            )
        }
    }

    fun reset() {
        navigator.removeAlarm()
        viewModelScope.launch {
            isStarted.value = false
            isRunning.value = false
            remainingTime.value = 0L
            startTime = 0L
            dataStoreManager.resetTimer()
            viewModelScope.coroutineContext.cancelChildren()
        }
    }

    private val isStarted = MutableLiveData(false)
    private val isRunning = MutableLiveData(false)
    private val remainingTime = MutableLiveData(0L)

    val isStartedI: LiveData<Boolean> = isStarted
    val isRunningI: LiveData<Boolean> = isRunning
    val remainingTimeI: LiveData<Long> = remainingTime
}