package com.justdeax.composeTimer.timer
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import com.justdeax.composeTimer.AppActivity
import com.justdeax.composeTimer.R

class TimerReceiver : BroadcastReceiver() {
    private val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    else
        PendingIntent.FLAG_UPDATE_CURRENT

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            val pendingIntent = PendingIntent.getActivity(
                ctx,
                1,
                Intent(ctx, AppActivity::class.java),
                flag
            )
            val notification = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(ctx, R.string.timer_finished))
                .setContentText(getString(ctx, R.string.timer_expired))
                .setContentIntent(pendingIntent)
                .build()

            val notificationManager = getSystemService(ctx, NotificationManager::class.java) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(ctx, R.string.timer_channel),
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 2280
        private const val NOTIFICATION_CHANNEL_ID = "timer_receiver_channel"
    }
}

interface AlarmSettingsNavigator {
    fun setAlarm(timeInMillis: Long)
    fun removeAlarm()
}
