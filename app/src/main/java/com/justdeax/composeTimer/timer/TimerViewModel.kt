package com.justdeax.composeTimer.timer
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft
    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning

    private var alarmManager: AlarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var timeRemaining: Long = 0L

    private val pendingIntent: PendingIntent
        get() = PendingIntent.getBroadcast(
            getApplication(),
            0,
            Intent(getApplication(), TimerReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun startResume(duration: Long) {
        if (_isRunning.value!!) {
            _timeLeft.value = duration
            timeRemaining = duration
        } else {
            _isRunning.value = false
        }

        val triggerAtMillis = System.currentTimeMillis() + timeRemaining
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (_: SecurityException) { }
    }

    fun pauseTimer() {
        _isRunning.value = false
        alarmManager.cancel(pendingIntent)
    }

    fun cancelTimer() {
        _isRunning.value = false
        alarmManager.cancel(pendingIntent)
        _timeLeft.value = 0L
    }

    fun updateTimeLeft(timeLeft: Long) {
        _timeLeft.value = timeLeft
        timeRemaining = timeLeft
    }
}

//class CountdownTimerViewModel(private val context: Context) : ViewModel() {
//    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    private var startTimeMillis = 0L
//
//    fun setTime(time: Long) { timeRemaining.value = time }
//
//    fun startResume() {
//        startTimeMillis = System.currentTimeMillis()
//
//        val intent = Intent(context, TimerExpiredReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        val triggerTime = startTimeMillis + timeRemaining.value!!
//
//        if (canScheduleExactAlarms(alarmManager)) {
//            try {
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
//                isStarted.value = true
//                isPaused.value = false
//                updateUITimer()
//            } catch (e: SecurityException) {
//                Toast.makeText(context, "Cannot schedule exact alarm: permission not granted", Toast.LENGTH_SHORT).show()
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//            context.startActivity(intent)
//        }
//    }
//
//    fun pause() {
//        val elapsedMillis = System.currentTimeMillis() - startTimeMillis
//        timeRemaining.value = timeRemaining.value!! - elapsedMillis
//        reset()
//        isPaused.value = true
//    }
//
//    fun reset() {
//        val intent = Intent(context, TimerExpiredReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        alarmManager.cancel(pendingIntent)
//        isStarted.value = false
//    }
//
//    private fun updateUITimer() {
//        viewModelScope.launch {
//            while (isStarted.value!! && !isPaused.value!!) {
//                val elapsedMillis = System.currentTimeMillis() - startTimeMillis
//                val remainingMillis = timeRemaining.value!! - elapsedMillis
//                timeRemaining.value = remainingMillis
//
//                if (remainingMillis <= 0) {
//                    isStarted.value = false
//                    break
//                }
//
//                delay(1000L)
//            }
//        }
//    }
//
//    private val isStarted = MutableLiveData(false)
//    private val isPaused = MutableLiveData(false)
//    private val timeRemaining = MutableLiveData(0L)
//    //private val elapsedSec = MutableLiveData(0L)
//
//    val isStartedI: LiveData<Boolean> get() = isStarted
//    val isPausedI: LiveData<Boolean> get() = isPaused
//    val timeRemainingI: LiveData<Long> get() = timeRemaining
//    //val elapsedSecI: LiveData<Long> get() = elapsedSec
//}
