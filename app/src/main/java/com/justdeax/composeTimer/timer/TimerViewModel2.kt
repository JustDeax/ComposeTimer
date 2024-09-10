package com.justdeax.composeTimer.timer
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TimerViewModel2(application: Application) : AndroidViewModel(application) {
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var alarmManager: AlarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val remainingTime = MutableLiveData<Long>()
    private val isRunning = MutableLiveData<Boolean>()
    private val isStarted = MutableLiveData<Boolean>()

    val remainingTimeI: LiveData<Long> = remainingTime
    val isRunningI: LiveData<Boolean> = isRunning
    val isStartedI: LiveData<Boolean> = isStarted

    init {
        isRunning.value = false
        isStarted.value = false
    }

    //TODO Remove timeInMillis parameters and merge timeLeft and Remaining Time variables
    fun startResume(timeInMillis: Long) {
        isStarted.value = true
        isRunning.value = true
        timeLeftInMillis = timeInMillis

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime.value = millisUntilFinished
                timeLeftInMillis = millisUntilFinished
            }
            override fun onFinish() {
                isRunning.value = false
                isStarted.value = false
            }
        }.start()
        setAlarm(timeInMillis)
    }

    fun pause() {
        countDownTimer?.cancel()
        isRunning.value = false
    }

    fun reset() {
        countDownTimer?.cancel()
        remainingTime.value = 0
        isStarted.value = false
        isRunning.value = false
    }

    private fun setAlarm(timeInMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            try {
                if (alarmManager.canScheduleExactAlarms())
                    scheduleExactAlarm(timeInMillis)
                else
                    openExactAlarmSettings()
            } catch (e: SecurityException) {
                Log.e("TimerViewModel", "SecurityException: Unable to schedule exact alarm. ${e.message}")
                openExactAlarmSettings()
            }
        else
            scheduleExactAlarm(timeInMillis)
    }

    private fun scheduleExactAlarm(timeInMillis: Long) {
        val alarmIntent = Intent(getApplication(), TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + timeInMillis,
            pendingIntent
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun openExactAlarmSettings() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        getApplication<Application>().startActivity(intent)
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