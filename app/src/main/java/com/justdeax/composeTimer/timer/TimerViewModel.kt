package com.justdeax.composeTimer.timer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    val foregroundEnabled = dataStoreManager.foregroundEnabled().asLiveData()
    val lockAwakeEnabled = dataStoreManager.lockAwakeEnabled().asLiveData()

    fun changeTheme(themeCode: Int) = viewModelScope.launch {
        dataStoreManager.changeTheme(themeCode)
    }

    fun changeForegroundEnabled(enabled: Boolean) = viewModelScope.launch {
        dataStoreManager.changeForegroundEnabled(enabled)
    }

    fun changeLockAwakeEnabled(enabled: Boolean) = viewModelScope.launch {
        dataStoreManager.changeLockAwakeEnabled(enabled)
    }

    fun saveEditTime() {
        viewModelScope.launch {
            dataStoreManager.saveEditTime(editTime, position)
        }
    }

    fun restoreEditTime() {
        viewModelScope.launch {
            dataStoreManager.restoreEditTime().collect { restoreState ->
                editTime = restoreState.first
                position = restoreState.second
            }
        }
    }

    fun restoreTimer() {
        viewModelScope.launch {
            dataStoreManager.restoreTimer().collect { restoredState ->
                remainingMs.value = restoredState.timerDuration
                isStarted.value = restoredState.timerDuration != 0L
                isRunning.value = restoredState.isRunning
                if (isRunning.value!!) {
                    startTime = restoredState.startTime
                    startResume(remainingMs.value!!)
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
            dataStoreManager.saveTimer(TimerState(timerDuration, startTime, isRunning.value!!))
            while (isRunning.value!!) {
                val deltaTime = System.currentTimeMillis() - startTime
                if (deltaTime >= timerDuration)
                    reset()
                else
                    remainingMs.postValue(timerDuration - deltaTime)
                val seconds = remainingMs.value!! / 1000
                if (remainingSec.value != seconds) remainingSec.postValue(seconds)
                delay(100L)
            }
        }
    }

    fun pause() {
        isRunning.value = false
        navigator.removeAlarm()
        startTime = 0L
        viewModelScope.launch {
            dataStoreManager.saveTimer(TimerState(remainingMs.value!!, startTime, false))
        }
    }

    fun reset() {
        navigator.removeAlarm()
        viewModelScope.launch {
            isStarted.value = false
            isRunning.value = false
            remainingMs.value = 0L
            remainingSec.value = 0L
            startTime = 0L
            dataStoreManager.resetTimer()
            viewModelScope.coroutineContext.cancelChildren()
        }
    }

    fun clearEditText() {
        editTime = "000000"
        position = 0
        saveEditTime()
    }

    fun appendEditText(number: Char) {
        if (position < 6) {
            val charArray = editTime.toCharArray()
            charArray[position] = number
            editTime = String(charArray)
            position++
        }
    }

    fun backspaceEditText() {
        if (position != 0) {
            position--
            val charArray = editTime.toCharArray()
            charArray[position] = '0'
            editTime = String(charArray)
        }
    }

    var position by mutableIntStateOf(0)
    var editTime by mutableStateOf("000000")

    private val isStarted = MutableLiveData(false)
    private val isRunning = MutableLiveData(false)
    private val remainingMs = MutableLiveData(0L)
    private val remainingSec = MutableLiveData(0L)

    val isStartedI: LiveData<Boolean> = isStarted
    val isRunningI: LiveData<Boolean> = isRunning
    val remainingMsI: LiveData<Long> = remainingMs
    val remainingSecI: LiveData<Long> = remainingSec
}