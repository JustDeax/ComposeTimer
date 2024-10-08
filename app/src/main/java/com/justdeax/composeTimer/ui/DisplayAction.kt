package com.justdeax.composeTimer.ui
import android.os.Build
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.justdeax.composeTimer.R
import kotlinx.coroutines.delay

@Composable
fun DisplayActions(
    modifier: Modifier,
    isPortrait: Boolean,
    isStarted: Boolean,
    show: Boolean,
    foregroundEnabled: Boolean,
    changeForegroundEnabled: () -> Unit,
    reset: () -> Unit,
    theme: Int,
    changeTheme: (Int) -> Unit,
    lockAwakeEnabled: Boolean,
    changeLockAwakeEnabled: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val savedTimersDraw = painterResource(R.drawable.round_casino_24)
    val addStopwatchDraw = painterResource(R.drawable.round_add_circle_24)
    val turnOffNotifDraw = painterResource(R.drawable.round_notifications_24)
    val turnOnNotifDraw = painterResource(R.drawable.round_notifications_none_24)
    val themeDraw = painterResource(R.drawable.round_invert_colors_24)
    val unlockAwake = painterResource(R.drawable.round_lock_outline_24)
    val lockAwake = painterResource(R.drawable.round_lock_24)

    var showSavedTimersDialog by remember { mutableStateOf(false) }
    var showResetStopwatchDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLockAwakeDialog by remember { mutableStateOf(false) }

    @Composable
    fun actionDialogs(modifier: Modifier) {
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                if (isStarted)
                    showSavedTimersDialog = true
                else
                    Log.e("TAG", "addTimerDialog")
            },
            painter = if (isStarted) addStopwatchDraw else savedTimersDraw,
            contentDesc =
            if (isStarted)
                context.getString(R.string.multi_timer)
            else
                context.getString(R.string.add_timer)
        )
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                showResetStopwatchDialog = true
            },
            painter = if (foregroundEnabled) turnOffNotifDraw else turnOnNotifDraw,
            contentDesc = context.getString(R.string.turn_off_notif)
        )
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                showThemeDialog = true
            },
            painter = themeDraw,
            contentDesc = context.getString(R.string.theme)
        )
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                changeLockAwakeEnabled(!lockAwakeEnabled)
                showLockAwakeDialog = true
            },
            painter = if (lockAwakeEnabled) lockAwake else unlockAwake,
            contentDesc = context.getString(R.string.lock_awake)
        )
    }

//    if (showTapOnClockDialog) {
//        RadioDialog(
//            title = activity.getString(R.string.change_tap_on_clock),
//            desc = activity.getString(R.string.change_tap_on_clock_desc),
//            isPortrait = isPortrait,
//            defaultIndex = activity.viewModel.tapOnClock.value!!,
//            options = activity.resources.getStringArray(R.array.tap_on_clock),
//            setSelectedIndex = { newState -> activity.viewModel.changeTapOnClock(newState)},
//            onDismiss = { showTapOnClockDialog = false },
//            onConfirm = { showTapOnClockDialog = false },
//            confirmText = activity.getString(R.string.apply)
//        )
//    }
    if (showResetStopwatchDialog) {
        if (isStarted)
            SimpleDialog(
                title = context.getString(R.string.reset_stopwatch),
                desc = if (foregroundEnabled)
                    context.getString(R.string.reset_stopwatch_desc_disable)
                else
                    context.getString(R.string.reset_stopwatch_desc_enable),
                isPortrait = isPortrait,
                confirmText = context.getString(R.string.ok),
                onConfirm = {
                    if (foregroundEnabled) //context.commandService(TimerAction.RESET)
                    else reset()
                    changeForegroundEnabled()
                    showResetStopwatchDialog = false
                },
                dismissText = context.getString(R.string.cancel),
                onDismiss = { showResetStopwatchDialog = false }
            )
        else
            changeForegroundEnabled()
    }
    if (showThemeDialog) {
        RadioDialog(
            title = context.getString(R.string.change_theme),
            isPortrait = isPortrait,
            desc = context.getString(R.string.change_theme_desc),
            defaultIndex = theme,
            options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                context.resources.getStringArray(R.array.theme12)
            else
                context.resources.getStringArray(R.array.theme),
            setSelectedIndex = { newState -> changeTheme(newState)},
            onDismiss = { showThemeDialog = false },
            onConfirm = { showThemeDialog = false },
            confirmText = context.getString(R.string.apply)
        )
    }
    if (showLockAwakeDialog) {
        LaunchedEffect(Unit) {
            delay(2000)
            showLockAwakeDialog = false
        }

        OkayDialog(
            title = context.getString(R.string.lock_awake_mode),
            content = {
                Text(
                    text = if (lockAwakeEnabled)
                        context.getString(R.string.lock_awake_mode_desc_enable)
                    else
                        context.getString(R.string.lock_awake_mode_desc_disable),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            isPortrait = isPortrait,
            confirmText = context.getString(R.string.ok),
            onConfirm = { showLockAwakeDialog = false }
        )
    }

    if (isPortrait)
        androidx.compose.animation.AnimatedVisibility(
            visible = show,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 80 },
            exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { 80 }
        ) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) { actionDialogs(Modifier.weight(1f)) }
        }
    else
        androidx.compose.animation.AnimatedVisibility(
            visible = show,
            enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { -80 },
            exit = fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -80 }
        ) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.SpaceEvenly
            ) { actionDialogs(Modifier.weight(1f)) }
        }
}