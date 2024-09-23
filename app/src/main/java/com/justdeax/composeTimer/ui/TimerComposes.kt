package com.justdeax.composeTimer.ui
import android.os.Build
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.justdeax.composeTimer.AppActivity
import com.justdeax.composeTimer.R
import com.justdeax.composeTimer.util.displayMs
import com.justdeax.composeTimer.util.formatSeconds
import kotlinx.coroutines.launch

@Composable
fun DisplayTime(
    modifier: Modifier,
    miniClock: Boolean,
    isPausing: Boolean,
    seconds: Long,
    milliseconds: Long
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val blinkAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .alpha(if (isPausing) blinkAnimation else 1f)
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            if (miniClock) {
                Text(
                    text = "${formatSeconds(seconds)}.",
                    fontSize = 60.sp,
                    fontFamily = FontFamily.Monospace,
                )
                Text(
                    text = displayMs(milliseconds),
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.offset(y = 30.dp)
                )
            } else {
                Text(
                    text = "${formatSeconds(seconds)}.",
                    fontSize = 90.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = displayMs(milliseconds),
                    fontSize = 60.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.offset(y = 45.dp)
                )
            }
        }
    }
}

@Composable
fun DisplayEditTime(
    modifier: Modifier,
    miniClock: Boolean,
    timeText: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (miniClock) {
            Text(
                modifier = Modifier
                    .alpha(0.5f)
                    .padding(4.dp),
                text = timeText.substring(0, 2) + "h",
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = timeText.substring(2, 4) + "m",
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = timeText.substring(4, 6) + "s",
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace,
            )
        } else {
            Text(
                modifier = Modifier
                    .alpha(0.5f)
                    .padding(4.dp),
                text = timeText.substring(0, 2) + "h",
                fontSize = 90.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = timeText.substring(2, 4) + "m",
                fontSize = 90.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = timeText.substring(4, 6) + "s",
                fontSize = 90.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
fun DisplayActions(
    modifier: Modifier,
    activity: AppActivity,
    isPortrait: Boolean,
    show: Boolean,
    foregroundEnabled: Boolean,
    lockAwakeEnabled: Boolean
) {
    val tapOnClockDraw = painterResource(R.drawable.round_adjust_24)
    val turnOffNotifDraw = painterResource(R.drawable.round_notifications_24)
    val turnOnNotifDraw = painterResource(R.drawable.round_notifications_none_24)
    val themeDraw = painterResource(R.drawable.round_invert_colors_24)
    val unlockAwake = painterResource(R.drawable.round_lock_outline_24)
    val lockAwake = painterResource(R.drawable.round_lock_24)

    var showTapOnClockDialog by remember { mutableStateOf(false) }
    var showResetStopwatchDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLockAwakeDialog by remember { mutableStateOf(false) }

    @Composable
    fun actionDialogs(modifier: Modifier) {
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                showTapOnClockDialog = true
            },
            painter = tapOnClockDraw,
            contentDesc = activity.getString(R.string.multi_timer)
        )
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                showResetStopwatchDialog = true
            },
            painter = if (foregroundEnabled) turnOffNotifDraw else turnOnNotifDraw,
            contentDesc = activity.getString(R.string.turn_off_notif)
        )
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                showThemeDialog = true
            },
            painter = themeDraw,
            contentDesc = activity.getString(R.string.theme)
        )
        OutlineIconButton(
            modifier = modifier,
            onClick = {
                activity.viewModel.changeLockAwakeEnabled(!lockAwakeEnabled)
                showLockAwakeDialog = true
            },
            painter = if (lockAwakeEnabled) lockAwake else unlockAwake,
            contentDesc = activity.getString(R.string.lock_awake)
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
        if (!foregroundEnabled && activity.viewModel.remainingMsI.value!! == 0L) {
            activity.viewModel.changeForegroundEnabled(true)
        } else {
            SimpleDialog(
                title = activity.getString(R.string.reset_stopwatch),
                desc = if (foregroundEnabled)
                    activity.getString(R.string.reset_stopwatch_desc_disable)
                else
                    activity.getString(R.string.reset_stopwatch_desc_enable),
                isPortrait = isPortrait,
                confirmText = activity.getString(R.string.ok),
                onConfirm = {
                    if (foregroundEnabled)
                        activity.lifecycleScope.launch {
                            //activity.commandService(StopwatchAction.RESET)
                            activity.viewModel.changeForegroundEnabled(false)
                            showResetStopwatchDialog = false
                        }
                    else
                        activity.lifecycleScope.launch {
                            activity.viewModel.reset()
                            activity.viewModel.changeForegroundEnabled(true)
                            showResetStopwatchDialog = false
                        }
                },
                dismissText = activity.getString(R.string.cancel),
                onDismiss = { showResetStopwatchDialog = false }
            )
        }
    }
    if (showThemeDialog) {
        RadioDialog(
            title = activity.getString(R.string.change_theme),
            isPortrait = isPortrait,
            desc = activity.getString(R.string.change_theme_desc),
            defaultIndex = activity.viewModel.theme.value!!,
            options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                activity.resources.getStringArray(R.array.theme12)
            else
                activity.resources.getStringArray(R.array.theme),
            setSelectedIndex = { newState -> activity.viewModel.changeTheme(newState)},
            onDismiss = { showThemeDialog = false },
            onConfirm = { showThemeDialog = false },
            confirmText = activity.getString(R.string.apply)
        )
    }
    if (showLockAwakeDialog) {
        OkayDialog(
            title = activity.getString(R.string.lock_awake_mode),
            content = {
                Text(
                    text = if (lockAwakeEnabled)
                        activity.getString(R.string.lock_awake_mode_desc_enable)
                    else
                        activity.getString(R.string.lock_awake_mode_desc_disable),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            isPortrait = isPortrait,
            confirmText = activity.getString(R.string.ok),
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

@Composable
fun DisplayKeyboard(
    modifier: Modifier,
    activity: AppActivity,
    isRunning: Boolean,
    isStarted: Boolean,
    remainingTime: Long,
    notificationEnabled: Boolean,
    isAdditionalsShow: Boolean,
    showHideAdditionals: () -> Unit
) {
    val backspaceDrawable = painterResource(R.drawable.round_backspace_24)
    val startDrawable = painterResource(R.drawable.round_play_arrow_24)
    val pauseDrawable = painterResource(R.drawable.round_pause_24)
    val stopDrawable = painterResource(R.drawable.round_stop_24)
//    val startButtonSizeAnimation by animateIntAsState(
//        targetValue = if (isStarted) 120 else 300,
//        animationSpec = keyframes { durationMillis = 250 },
//        label = ""
//    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val numberOfColumns = 3
        Column {
            if (!isStarted)
                repeat(numberOfColumns) { rowIndex ->
                    Row {
                        repeat(3) { columnIndex ->
                            val number = rowIndex * numberOfColumns + columnIndex + 1
                            BaseButton(100, 100,
                                { activity.viewModel.appendEditText((number + '0'.code).toChar()) }
                            ) {
                                Text(
                                    text = number.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            Row {
                IconButton(
                    painter = backspaceDrawable,
                    contentDesc = activity.getString(R.string.backspace)
                ) {
                    activity.viewModel.backspaceEditTet()
//                    if (isRunning)
//                        activity.viewModel.pause()
//                    else
//                        activity.viewModel.startResume(remainingTime)
                }
                BaseButton(100, 85,
                    { activity.viewModel.appendEditText('0') }
                ) {
                    Text(
                        text = "0",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(
                    painter = if (isRunning) pauseDrawable else startDrawable,
                    contentDesc =
                    if (isRunning) activity.getString(R.string.pause)
                    else activity.getString(R.string.resume)
                ) {
                    if (isRunning)
                        activity.viewModel.reset()
                    else {
                        activity.viewModel.startResume(20*1000)
                    }
                }
            }
        }
    }
}