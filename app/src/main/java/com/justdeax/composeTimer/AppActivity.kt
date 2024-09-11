package com.justdeax.composeTimer
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.justdeax.composeTimer.timer.TimerViewModel2
import com.justdeax.composeTimer.timer.TimerViewModelFactory2
import com.justdeax.composeTimer.ui.DisplayTime
import com.justdeax.composeTimer.ui.theme.DarkColorScheme
import com.justdeax.composeTimer.ui.theme.ExtraDarkColorScheme
import com.justdeax.composeTimer.ui.theme.LightColorScheme
import com.justdeax.composeTimer.ui.theme.Typography

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppScreen() }
    }

    @Composable
    fun AppScreen() {
        val context = LocalContext.current.applicationContext as Application
        val viewModel: TimerViewModel2 = viewModel(
            factory = TimerViewModelFactory2(context)
        )
        TimerScreen(viewModel)
    }

    @Composable
    fun TimerScreen(viewModel: TimerViewModel2) {
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
                        else
                            viewModel.startResume(60 * 1000)
                    }) {
                        Text("START/PAUSE")
                    }
                }
            }
        }
    }
}