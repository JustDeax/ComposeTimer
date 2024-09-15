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
    private var timeBeforePause = 0L
    private var startTime = 0L
    private var timerDuration = 0L
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

    fun saveTimer() {
        viewModelScope.launch {
            dataStoreManager.saveTimer(
                TimerState(
                    remainingTime.value!!,
                    isRunning.value!!
                )
            )
        }
    }

    fun restoreTimer() {
        viewModelScope.launch {
            dataStoreManager.restoreTimer().collect { restoredState ->
                remainingTime.value = restoredState.remainingTime
                isStarted.value = restoredState.remainingTime != 0L
                isRunning.value = restoredState.isRunning
                if (isRunning.value!!)
                    startResume()
            }
        }
    }


    fun setTime(time: Long) {
        timerDuration = time
    }

    fun startResume() {
        isStarted.value = true
        isRunning.value = true
        navigator.setAlarm(remainingTime.value ?: 1000L)
        viewModelScope.launch(Dispatchers.IO) {
            if (startTime == 0L) startTime = System.currentTimeMillis()
            while (isRunning.value!!) {
                val deltaTime = System.currentTimeMillis() - startTime
                remainingTime.postValue((timerDuration - deltaTime) + timeBeforePause)
                delay(100L)
            }
        }
//        countDownTimer = object : CountDownTimer(remainingTime.value ?: 1000L, 100) {
//            override fun onTick(millisUntilFinished: Long) {
//                remainingTime.value = millisUntilFinished
//            }
//            override fun onFinish() {
//                isRunning.value = false
//                isStarted.value = false
//            }
//        }.start()
    }

    fun pause() {
        navigator.removeAlarm()
        isRunning.value = false
        timeBeforePause = remainingTime.value!!
    }

    fun reset(timeInMillis: Long) {
        navigator.removeAlarm()
        viewModelScope.launch {
            isStarted.value = false
            isRunning.value = false
            timerDuration = timeInMillis
            remainingTime.value = 0L
            timeBeforePause = 0L
            startTime = 0L
            dataStoreManager.resetStopwatch()
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