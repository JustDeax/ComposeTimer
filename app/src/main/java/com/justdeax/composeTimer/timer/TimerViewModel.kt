package com.justdeax.composeTimer.timer
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.justdeax.composeTimer.util.DataStoreManager
import com.justdeax.composeTimer.util.TimerState
import kotlinx.coroutines.launch

class TimerViewModel(
    private val dataStoreManager: DataStoreManager,
    private val navigator: AlarmSettingsNavigator
) : ViewModel() {
    private var countDownTimer: CountDownTimer? = null
    val theme = dataStoreManager.getTheme().asLiveData()
    val tapOnClock = dataStoreManager.getTapOnClock().asLiveData()
    val foregroundEnabled = dataStoreManager.foregroundEnabled().asLiveData()

    fun changeTheme(themeCode: Int) {
        viewModelScope.launch {
            dataStoreManager.changeTheme(themeCode)
        }
    }

    fun changeTapOnClock(tapType: Int) {
        viewModelScope.launch {
            dataStoreManager.changeTapOnClock(tapType)
        }
    }

    fun changeForegroundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.changeForegroundEnabled(enabled)
        }
    }

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
        remainingTime.value = time
    }

    fun startResume() {
        isStarted.value = true
        isRunning.value = true
        navigator.setAlarm(remainingTime.value ?: 1000L)
        countDownTimer = object : CountDownTimer(remainingTime.value ?: 1000L, 100) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime.value = millisUntilFinished
            }
            override fun onFinish() {
                isRunning.value = false
                isStarted.value = false
            }
        }.start()
    }

    fun pause() {
        navigator.removeAlarm()
        countDownTimer?.cancel()
        isRunning.value = false
    }

    fun reset(timeInMillis: Long) {
        navigator.removeAlarm()
        countDownTimer?.cancel()
        remainingTime.value = timeInMillis
        isStarted.value = false
        isRunning.value = false
    }

    private val isStarted = MutableLiveData(false)
    private val isRunning = MutableLiveData(false)
    private val remainingTime = MutableLiveData(0L)

    val isStartedI: LiveData<Boolean> = isStarted
    val isRunningI: LiveData<Boolean> = isRunning
    val remainingTimeI: LiveData<Long> = remainingTime
}