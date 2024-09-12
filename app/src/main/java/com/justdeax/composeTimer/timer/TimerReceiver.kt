package com.justdeax.composeTimer.timer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //Timer finished
        //TODO MAKE NOTIFICATION AND CANCEL PENDING INTENT
    }
}

interface AlarmSettingsNavigator {
    fun setAlarm(timeInMillis: Long)
}
