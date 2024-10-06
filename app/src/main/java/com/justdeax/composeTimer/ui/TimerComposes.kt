package com.justdeax.composeTimer.ui
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justdeax.composeTimer.R
import com.justdeax.composeTimer.util.toFormatString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DisplayTime(
    modifier: Modifier,
    miniClock: Boolean,
    isPausing: Boolean,
    seconds: Long
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
                .wrapContentSize()
        ) {
            if (miniClock) {
                Text(
                    text = seconds.toFormatString(),
                    fontSize = 60.sp,
                    fontFamily = FontFamily.Monospace,
                )
            } else {
                Text(
                    text = seconds.toFormatString(),
                    fontSize = 90.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun DisplayEditTime(
    modifier: Modifier,
    editTime: String,
    position: Int
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.wrapContentSize()) {
            val hoursText = buildTimeText(editTime, position, 0, 1, "h")
            val minutesText = buildTimeText(editTime, position, 2, 3, "m")
            val secondsText = buildTimeText(editTime, position, 4, 5, "s")

            Text(
                modifier = Modifier.padding(4.dp),
                text = hoursText,
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = minutesText,
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = secondsText,
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

private fun buildTimeText(
    editTime: String,
    position: Int,
    firstIndex: Int,
    secondIndex: Int,
    suffix: String
): AnnotatedString {
    val spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
    return buildAnnotatedString {
        when (position) {
            firstIndex -> {
                withStyle(spanStyle) { append(editTime[firstIndex]) }
                append(editTime[secondIndex])
            }
            secondIndex -> {
                append(editTime[firstIndex])
                withStyle(spanStyle) { append(editTime[secondIndex]) }
            }
            else -> {
                append(editTime[firstIndex])
                append(editTime[secondIndex])
            }
        }
        append(suffix)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayAppName(
    modifier: Modifier,
    show: Boolean
) {
    val context = LocalContext.current
    val helpDraw = painterResource(R.drawable.round_help_outline_24)
    var showAboutApp by remember { mutableStateOf(false) }

    androidx.compose.animation.AnimatedVisibility(
        visible = show,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -40 },
        exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { -40 }
    ) {
        Row(
            modifier = modifier.clickable(
                remember { MutableInteractionSource() }, null
            ) { showAboutApp = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = context.getString(R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                modifier = Modifier.size(24.dp),
                painter = helpDraw,
                contentDescription = context.getString(R.string.about_app),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    if (showAboutApp) {
        ModalBottomSheet(
            onDismissRequest = { showAboutApp = false },
            sheetState = sheetState,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 2.dp)
            ) {
                val scrollState = rememberScrollState()
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = context.getString(R.string.about_app),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = context.getString(R.string.about_app_desc),
                        style = MaterialTheme.typography.titleMedium
                    )
                    val annotatedString = buildAnnotatedString {
                        append(context.getString(R.string.about_app_desc_a))
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(" " + context.getString(R.string.app_author))
                        }
                        append(context.getString(R.string.about_app_desc_v))
                        append(" " + context.getString(R.string.app_version))
                    }
                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/JustDeax"))
                            context.startActivity(intent)
                        }
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    onClick = {
                        scope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) showAboutApp = false
                            }
                    }
                ) {
                    Text(context.getString(R.string.ok))
                }
            }
        }
    }
}

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

@Composable
fun DisplayKeyboard(
    modifier: Modifier,
    startResume: (Long) -> Unit,
    appendEditText: (Char) -> Unit,
    backspaceEditText: () -> Unit,
    clearEditText: () -> Unit,
    editTime: String,
    position: Int
) {
    val context = LocalContext.current
    val startDrawable = painterResource(R.drawable.round_play_arrow_24)
//    val pauseDrawable = painterResource(R.drawable.round_pause_24)
//    val stopDrawable = painterResource(R.drawable.round_stop_24)
//    val additionalsDrawable = painterResource(R.drawable.round_grid_view_24)
    val backspaceDrawable = painterResource(R.drawable.round_backspace_24)
//    val heightAnimation by animateDpAsState(
//        targetValue = if (isStarted) 0.dp else 100.dp,
//        animationSpec = tween(500),
//        label = ""
//    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val numberOfColumns = 3
        Column {
            repeat(numberOfColumns) { rowIndex ->
                Row(modifier = Modifier.height(100.dp)) {
                    repeat(3) { columnIndex ->
                        val number = rowIndex * numberOfColumns + columnIndex + 1
                        BaseButton(
                            width = 100,
                            height = 100,
                            isMustBeAnimated = true,
                            onClick = { appendEditText((number + '0'.code).toChar()) }
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
                    contentDesc = context.getString(R.string.backspace),
                    isMustBeAnimated = true
                ) { backspaceEditText() }
                BaseButton(
                    width = 100,
                    height = 86,
                    isMustBeAnimated = true,
                    onClick = {
                        if (position < 6)
                            appendEditText('0')
                        else
                            clearEditText()
                    }
                ) {
                    Text(
                        text = if (position < 6) "0" else "C",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                BaseButton(
                    width = 100,
                    height = 86,
                    isMustBeAnimated = false,
                    {
                        if (editTime == "000000") {
                            appendEditText('0')
                            appendEditText('0')
                        } else {
                            val hours = editTime.substring(0, 2).toInt()
                            val minutes = editTime.substring(2, 4).toInt()
                            val seconds = editTime.substring(4, 6).toInt()
                            val startTime = hours * 3600 + minutes * 60 + seconds
                            startResume(startTime * 1000L)
                        }
                    }
                ) {
                    if (editTime == "000000")
                        Text(
                            text = "00",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    else
                        Icon(
                            painter = startDrawable,
                            contentDescription = context.getString(R.string.start),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                }
            }
        }
    }
}