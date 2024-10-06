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
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.justdeax.composeTimer.timer.AlarmSettingsNavigator
import com.justdeax.composeTimer.timer.TimerReceiver
import com.justdeax.composeTimer.timer.TimerViewModel
import com.justdeax.composeTimer.timer.TimerViewModelFactory
import com.justdeax.composeTimer.ui.DisplayAppName
import com.justdeax.composeTimer.ui.DisplayEditTime
import com.justdeax.composeTimer.ui.DisplayKeyboard
import com.justdeax.composeTimer.ui.theme.DarkColorScheme
import com.justdeax.composeTimer.ui.theme.ExtraDarkColorScheme
import com.justdeax.composeTimer.ui.theme.LightColorScheme
import com.justdeax.composeTimer.ui.theme.Typography
import com.justdeax.composeTimer.util.DataStoreManager
import com.justdeax.composeTimer.util.TimerState2
import com.justdeax.composeTimer.util.toFormatString

class AppActivity : ComponentActivity(), AlarmSettingsNavigator {
    private val viewModel: TimerViewModel by viewModels {
        TimerViewModelFactory(DataStoreManager(this), this)
    }
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent { AppScreen() }
    }

    private fun requestNotificationPermission() {
        alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
    }

    @Composable
    fun AppScreen() {
//        val foregroundEnabled by viewModel.foregroundEnabled.observeAsState(true)
//        if (foregroundEnabled) {
//            @Suppress("BooleanLiteralArgument")
//            TimerScreen(true, false, false, 0L, 0L)
//        } else {
//            LaunchedEffect(Unit) { viewModel.restoreTimer() }
//            val isStarted by viewModel.isStartedI.observeAsState(false)
//            val isRunning by viewModel.isRunningI.observeAsState(false)
//            val remainingMs by viewModel.remainingMsI.observeAsState(0L)
//            val remainingSec by viewModel.remainingSecI.observeAsState(0L)
//            TimerScreen(false, isStarted, isRunning, remainingMs, remainingSec)
//        }
        val timers = mutableListOf<TimerState2>()
        for (i in 0..2) {
            val timer = TimerState2(
                1200L, 710L, isStarted = false, isRunning = false, "20 Minute"
            )
            timers.add(timer)
        }
        var addTimerScreenShow by remember { mutableStateOf(false) }

        val theme by viewModel.theme.observeAsState(0)
        val lockAwakeEnabled by viewModel.lockAwakeEnabled.observeAsState(false)
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
                LaunchedEffect(lockAwakeEnabled) {
                    if (lockAwakeEnabled)
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    else
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                if (isPortrait) {
                    Column(Modifier.padding(innerPadding)) {
                        DisplayAppName(
                            Modifier.padding(21.dp, 16.dp),
                            true
                        )
                        if (addTimerScreenShow)
                            DisplayAddTimer()
                        else
                            DisplayTimers(timers) { addTimerScreenShow = true }
                        //DisplayOneTimer()

                    }
                }
            }
        }
    }

    @Composable
    fun DisplayTimers(
        timers: List<TimerState2>,
        changeAddTimerScreenShow: () -> Unit
    ) {
//SW
//Theme
//LockAwake
//ChangeTapOnClock
//(*) NotificationEnabled
//=== Records     Pause/Resume   AddLap/Stop

//CT
//Theme
//LockAwake
//Timer Ending
//(*) NotificationEnabled
//=== +1 minute  Pause/Resume   Stop

//FOR TIMER
//Timer sound
//Timer vibrate
//Epilepsy Enabled
//MAKE ANALYTICS
//Gradually increase volume ALWAYS TRUE
//Infinity sound ALWAYS TRUE
//Return Timer ALWAYS TRUE
        val addDraw = painterResource(R.drawable.round_add_circle_24)
        val playDraw = painterResource(R.drawable.round_play_arrow_24)
        val pauseDraw = painterResource(R.drawable.round_pause_24)
        val copyDraw = painterResource(R.drawable.round_content_copy_24)
        val plusOneDraw = painterResource(R.drawable.round_exposure_plus_1_24)
        val stopDraw = painterResource(R.drawable.round_stop_24)

        Box(contentAlignment = Alignment.BottomCenter) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(timers) { (timerDuration, remainingTime, isStarted, isRunning, name) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { (remainingTime.toFloat() / timerDuration) },
                                    modifier = Modifier
                                        .height(72.dp)
                                        .aspectRatio(1f),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 8.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceContainer
                                )
                                Icon(
                                    if (!isStarted || !isRunning) playDraw
                                    else pauseDraw,
                                    "",
                                    Modifier.size(38.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .padding(10.dp, 0.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text =
                                    if (isStarted) remainingTime.toFormatString()
                                    else timerDuration.toFormatString(),
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                if (!isStarted) copyDraw
                                else if (isRunning) plusOneDraw
                                else stopDraw,
                                "",
                                Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    changeAddTimerScreenShow()
                },
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .size(80.dp)
            ) {
                Icon(
                    addDraw,
                    "",
                    Modifier.size(28.dp)
                )
            }
        }
    }

    @Composable
    fun DisplayAddTimer() {
//        DisplayTime(
//            Modifier
//                .fillMaxSize()
//                .padding(10.dp)
//                .heightIn(min = 100.dp),
//            isStarted,
//            true,
//            !isRunning,
//            remainingSec,
//            remainingMs
//        )

        Column {
            DisplayEditTime(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                viewModel.editTime,
                viewModel.position
            )
            DisplayKeyboard(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 42.dp),
                { newState -> viewModel.startResume(newState) },
                { newState -> viewModel.appendEditText(newState) },
                { viewModel.backspaceEditText() },
                { viewModel.clearEditText() },
                viewModel.editTime,
                viewModel.position
            )
        }

//        DisplayActions(
//            Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(8.dp, 8.dp, 8.dp, 14.dp),
//            true,
//            isStarted,
//            !isStarted || additionalActionsShow,
//            foregroundEnabled,
//            { viewModel.changeForegroundEnabled(!foregroundEnabled) },
//            { viewModel.reset() },
//            theme,
//            { newState -> viewModel.changeTheme(newState) },
//            lockAwakeEnabled,
//            { newState -> viewModel.changeLockAwakeEnabled(newState)}
//        )
    }

    @Composable
    fun DisplayOneTimer() {

    }

//    @Composable
//    fun TimerScreen(
//        foregroundEnabled: Boolean,
//        isStarted: Boolean,
//        isRunning: Boolean,
//        remainingMs: Long,
//        remainingSec: Long
//    ) {
//        var additionalActionsShow by remember { mutableStateOf(false) }
//        val theme by viewModel.theme.observeAsState(0)
//        val lockAwakeEnabled by viewModel.lockAwakeEnabled.observeAsState(false)
//        val configuration = LocalConfiguration.current
//        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
//        val colorScheme = when (theme) {
//            1 -> LightColorScheme
//            2 -> DarkColorScheme
//            3 -> ExtraDarkColorScheme
//            else -> { // == 0
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    if (isSystemInDarkTheme())
//                        dynamicDarkColorScheme(this)
//                    else
//                        dynamicLightColorScheme(this)
//                } else {
//                    if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
//                }
//            }
//        }
//
//        MaterialTheme(colorScheme = colorScheme, typography = Typography) {
//            Scaffold(Modifier.fillMaxSize()) { innerPadding ->
//                LaunchedEffect(lockAwakeEnabled) {
//                    if (lockAwakeEnabled)
//                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//                    else
//                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//                }
//                if (isPortrait) {
//                    Column(Modifier.padding(innerPadding)) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .weight(1f),
//                            contentAlignment = Alignment.TopStart
//                        ) {
//                            DisplayAppName(
//                                Modifier.padding(21.dp, 16.dp),
//                                !isStarted
//                            )
//                            DisplayTime(
//                                Modifier
//                                    .fillMaxSize()
//                                    .padding(10.dp)
//                                    .heightIn(min = 100.dp),
//                                isStarted,
//                                true,
//                                !isRunning,
//                                remainingSec,
//                                remainingMs
//                            )
//                            DisplayEditTime(
//                                Modifier.fillMaxSize(),
//                                !isStarted,
//                                true,
//                                viewModel.editTime,
//                                viewModel.position
//                            )
//                        }
//                        DisplayActions(
//                            Modifier
//                                .fillMaxWidth()
//                                .wrapContentHeight()
//                                .padding(8.dp, 8.dp, 8.dp, 14.dp),
//                            true,
//                            isStarted,
//                            !isStarted || additionalActionsShow,
//                            foregroundEnabled,
//                            { viewModel.changeForegroundEnabled(!foregroundEnabled) },
//                            { viewModel.reset() },
//                            theme,
//                            { newState -> viewModel.changeTheme(newState) },
//                            lockAwakeEnabled,
//                            { newState -> viewModel.changeLockAwakeEnabled(newState)}
//                        )
//                        DisplayKeyboard(
//                            Modifier
//                                .fillMaxWidth()
//                                .padding(top = 12.dp, bottom = 42.dp),
//                            isStarted,
//                            isRunning,
//                            foregroundEnabled,
//                            additionalActionsShow,
//                            { additionalActionsShow = !additionalActionsShow },
//                            { viewModel.reset() },
//                            { newState -> viewModel.startResume(newState) },
//                            { viewModel.pause() },
//                            { newState -> viewModel.appendEditText(newState) },
//                            { viewModel.backspaceEditText() },
//                            { viewModel.clearEditText() },
//                            remainingMs,
//                            viewModel.editTime,
//                            viewModel.position
//                        )
//                    }
//                }
//            }
//        }
//    }

    override fun setAlarm(timeInMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            try {
                if (alarmManager.canScheduleExactAlarms())
                    scheduleExactAlarm(timeInMillis)
                else
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            } catch (e: SecurityException) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
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
}