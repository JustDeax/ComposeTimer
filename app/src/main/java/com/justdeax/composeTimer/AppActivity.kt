package com.justdeax.composeTimer
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.justdeax.composeTimer.timer.AlarmSettingsNavigator
import com.justdeax.composeTimer.timer.TimerReceiver
import com.justdeax.composeTimer.timer.TimerViewModel
import com.justdeax.composeTimer.timer.TimerViewModelFactory
import com.justdeax.composeTimer.ui.DisplayTime
import com.justdeax.composeTimer.ui.theme.DarkColorScheme
import com.justdeax.composeTimer.ui.theme.ExtraDarkColorScheme
import com.justdeax.composeTimer.ui.theme.LightColorScheme
import com.justdeax.composeTimer.ui.theme.Typography

class AppActivity : ComponentActivity(), AlarmSettingsNavigator {
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setContent { AppScreen() }
    }

    @Composable
    fun AppScreen() {
        val viewModel: TimerViewModel = viewModel(
            factory = TimerViewModelFactory(
                getSystemService(ALARM_SERVICE) as AlarmManager,
                this
            )
        )
        TimerScreen(viewModel)
    }

    @Composable
    fun TimerScreen(viewModel: TimerViewModel) {
        val isRunning by viewModel.isRunningI.observeAsState(false)
        val timeLeft by viewModel.remainingTimeI.observeAsState(0)
        val theme = 0
        @Suppress("KotlinConstantConditions")
        val colorScheme = when (theme) {
            1 -> LightColorScheme
            2 -> DarkColorScheme
            3 -> ExtraDarkColorScheme
            else -> { // == 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (isSystemInDarkTheme())
                        dynamicDarkColorScheme(this)
                    else
                        dynamicLightColorScheme(this)
                } else {
                    if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
                }
            }
        }
        MaterialTheme(colorScheme = colorScheme, typography = Typography) {
            Scaffold(Modifier.fillMaxSize()) { innerPadding ->
                Column(Modifier.padding(innerPadding)) {
                    DisplayTime(
                        Modifier
                            .animateContentSize()
                            .fillMaxWidth()
                            .padding(10.dp)
                            .heightIn(min = 100.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { /**TODO **/ },
                        true,
                        !isRunning,
                        timeLeft
                    )
                    Button(onClick = {
                        if (isRunning)
                            viewModel.pause()
                        else {
                            viewModel.setTime(63*1000)
                            viewModel.startResume()
                        }
                    }) {
                        Text("START/PAUSE")
                    }
                }
            }
        }
    }

    override fun setAlarm(timeInMillis: Long) {
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
        val alarmIntent = Intent(application, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
        startActivity(intent)
    }
}