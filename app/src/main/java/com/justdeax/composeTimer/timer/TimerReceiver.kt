package com.justdeax.composeTimer.timer
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.justdeax.composeTimer.R

class TimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TimerReceiver", "onReceive called")

        context?.let { ctx ->
             val notification = NotificationCompat.Builder(ctx, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Broadcast Received")
                .setContentText("Timer Finished")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            val notificationManager = getSystemService(ctx, NotificationManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "CHANNEL_ID",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager?.createNotificationChannel(channel)
            }
            notificationManager?.notify(2, notification)
        }
    }
}

interface AlarmSettingsNavigator {
    fun setAlarm(timeInMillis: Long)
    fun removeAlarm()
}
