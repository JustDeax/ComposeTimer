package com.justdeax.composeTimer
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.justdeax.composeTimer.timer.AlarmSettingsNavigator
import com.justdeax.composeTimer.timer.TimerReceiver
import com.justdeax.composeTimer.timer.TimerViewModel
import com.justdeax.composeTimer.timer.TimerViewModelFactory
import com.justdeax.composeTimer.ui.DisplayTime
import com.justdeax.composeTimer.ui.theme.DarkColorScheme
import com.justdeax.composeTimer.ui.theme.ExtraDarkColorScheme
import com.justdeax.composeTimer.ui.theme.LightColorScheme
import com.justdeax.composeTimer.ui.theme.Typography
import com.justdeax.composeTimer.util.DataStoreManager

class AppActivity : ComponentActivity(), AlarmSettingsNavigator {
    val viewModel: TimerViewModel by viewModels {
        TimerViewModelFactory(DataStoreManager(this), this)
    }
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        requestNotificationPermission()
        setContent { AppScreen() }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!viewModel.foregroundEnabled.value!!) viewModel.saveTimer()
    }

    @Composable
    fun AppScreen() {
        val foregroundEnabled by viewModel.foregroundEnabled.observeAsState(true)
        if (foregroundEnabled) {
            //TODO
            @Suppress("BooleanLiteralArgument")
            TimerScreen(true, false, false, 0L)
        } else {
            LaunchedEffect(Unit) {
                viewModel.restoreTimer()
            }
            val isStarted by viewModel.isStartedI.observeAsState(false)
            val isRunning by viewModel.isRunningI.observeAsState(false)
            val remainingTime by viewModel.remainingTimeI.observeAsState(0L)
            TimerScreen(false, isStarted, isRunning, remainingTime)
        }
    }

    @Composable
    fun TimerScreen(
        foregroundEnabled: Boolean,
        isStarted: Boolean,
        isRunning: Boolean,
        remainingTime: Long,
    ) {
        var additionalActionsShow by remember { mutableStateOf(false) }
        val theme by viewModel.theme.observeAsState(0)
        //val tapOnClock by viewModel.tapOnClock.observeAsState(0)
        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
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
                LaunchedEffect(Unit) {
                    if (!isStarted) additionalActionsShow = true
                }
                if (isPortrait) {
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
                            remainingTime
                        )
                        Button(onClick = {
                            if (isRunning)
                                viewModel.pause()
                            else {
                                viewModel.startResume()
                            }
                        }) {
                            Text("RESUME/PAUSE")
                        }

                        Button(onClick = {
                            if (isRunning)
                                viewModel.reset(6*1000)
                            else {
                                if (isStarted)
                                    viewModel.reset(6*1000)
                                viewModel.setTime(6*1000)
                                viewModel.startResume()
                            }
                        }) {
                            Text("START/STOP")
                        }
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

    override fun removeAlarm() {
        val alarmIntent = Intent(application, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleExactAlarm(timeInMillis: Long) {
        Log.d("TimerReceiver", "scheduleExactAlarm")
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
        Log.d("TimerReceiver", "openExactAlarmSettings")
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(intent)
    }
}