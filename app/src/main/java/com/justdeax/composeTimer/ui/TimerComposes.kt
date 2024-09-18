package com.justdeax.composeTimer.ui
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justdeax.composeTimer.AppActivity
import com.justdeax.composeTimer.R
import com.justdeax.composeTimer.util.displayMs
import com.justdeax.composeTimer.util.formatSeconds

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
                modifier = Modifier.alpha(0.5f).padding(4.dp),
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
                modifier = Modifier.alpha(0.5f).padding(4.dp),
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
fun DisplayKeyboard(
    modifier: Modifier,
    activity: AppActivity,
    isRunning: Boolean,
    remainingTime: Long
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
            repeat(numberOfColumns) { rowIndex ->
                Row {
                    repeat(3) { columnIndex ->
                        val number = rowIndex * numberOfColumns + columnIndex + 1
                        TextButton(number.toString()) {
                            activity.viewModel.appendEditText((number + '0'.code).toChar())
                        }
                    }
                }
            }
            Row {
                IconButton(
                    painter = backspaceDrawable,
                    contentDesc = activity.getString(R.string.backspace)
                ) {
                    if (isRunning)
                        activity.viewModel.pause()
                    else
                        activity.viewModel.startResume(remainingTime)
                }
                TextButton("0") {
                    activity.viewModel.appendEditText('0')
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