package com.justdeax.composeTimer.timer
import android.app.AlarmManager
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel2(
    private val alarmManager: AlarmManager,
    private val navigator: AlarmSettingsNavigator
) : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private val remainingTime = MutableLiveData(0L)
    private val isRunning = MutableLiveData(false)
    private val isStarted = MutableLiveData(false)

    val remainingTimeI: LiveData<Long> = remainingTime
    val isRunningI: LiveData<Boolean> = isRunning
    val isStartedI: LiveData<Boolean> = isStarted

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
        countDownTimer?.cancel()
        isRunning.value = false
    }

    fun reset(timeInMillis: Long) {
        countDownTimer?.cancel()
        remainingTime.value = timeInMillis
        isStarted.value = false
        isRunning.value = false
    }
}

//import android.os.CountDownTimer
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//
//class TimerViewModel2 : ViewModel() {
//    private var countDownTimer: CountDownTimer? = null
//
//    fun setTime(time: Long) {
//        remainingTime.value = time
//    }
//
//    fun startResume() {
//        isRunning.value = true
//        isStarted.value = true
//        countDownTimer = object : CountDownTimer(remainingTime.value!!, 100) {
//            override fun onTick(millisUntilFinished: Long) {
//                remainingTime.value = millisUntilFinished
//            }
//            override fun onFinish() {
//                remainingTime.value = 0
//                isRunning.value = false
//                isStarted.value = false
//            }
//        }.start()
//    }
//
//    fun pause() {
//        countDownTimer?.cancel()
//        isRunning.value = false
//    }
//
//    fun reset(resetTime: Long) {
//        countDownTimer?.cancel()
//        remainingTime.value = resetTime
//        isRunning.value = true
//        isStarted.value = true
//    }
//
//    private var isStarted = MutableLiveData(false)
//    private var isRunning = MutableLiveData(false)
//    private val remainingTime = MutableLiveData(0L)
//
//    val isStartedI: LiveData<Boolean> get() = isStarted
//    val isRunningI: LiveData<Boolean> get() = isRunning
//    val remainingTimeI: LiveData<Long> get() = remainingTime
//
//    override fun onCleared() {
//        super.onCleared()
//        countDownTimer?.cancel()
//    }
//}