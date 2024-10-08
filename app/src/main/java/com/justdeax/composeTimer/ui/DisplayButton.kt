package com.justdeax.composeTimer.ui
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.justdeax.composeTimer.R

@Composable
fun DisplayButton(
    modifier: Modifier,
    isStarted: Boolean,
    isRunning: Boolean,
    isAdditionalsShow: Boolean,
    showHideAdditionals: () -> Unit,
    reset: () -> Unit,
    startResume: (Long) -> Unit,
    pause: () -> Unit,
    appendEditText: (Char) -> Unit,
    backspaceEditText: () -> Unit,
    clearEditText: () -> Unit,
    remainingTime: Long,
    editTime: String,
    position: Int
) {
    val context = LocalContext.current
    val startDrawable = painterResource(R.drawable.round_play_arrow_24)
    val pauseDrawable = painterResource(R.drawable.round_pause_24)
    val stopDrawable = painterResource(R.drawable.round_stop_24)
    val additionalsDrawable = painterResource(R.drawable.round_grid_view_24)
    val backspaceDrawable = painterResource(R.drawable.round_backspace_24)

    Row(modifier = modifier) {
        IconButton(
            painter = if (isStarted) additionalsDrawable else backspaceDrawable,
            contentDesc =
            if (isStarted) context.getString(R.string.additional_action)
            else context.getString(R.string.backspace),
            isMustBeAnimated = !isStarted
        ) {
            if (isStarted)
                showHideAdditionals()
            else
                backspaceEditText()
        }
        BaseButton(
            width = 100,
            height = 86,
            isMustBeAnimated = !isStarted,
            {
                if (isStarted)
                    if (isRunning)
                        pause()
                    else
                        startResume(remainingTime)
                else
                    if (position < 6)
                        appendEditText('0')
                    else
                        clearEditText()
            }
        ) {
            if (isStarted)
                Icon(
                    painter = if (isRunning) pauseDrawable else startDrawable,
                    contentDescription =
                    if (isRunning) context.getString(R.string.pause)
                    else context.getString(R.string.resume),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            else
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
                    if (isAdditionalsShow) showHideAdditionals()
                    if (isStarted)
                        reset()
                    else {
                        val hours = editTime.substring(0, 2).toInt()
                        val minutes = editTime.substring(2, 4).toInt()
                        val seconds = editTime.substring(4, 6).toInt()
                        val startTime = hours * 3600 + minutes * 60 + seconds
                        Log.d("TAG", ": START :")
                        startResume(startTime * 1000L)
                    }
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
                    painter = if (isStarted) stopDrawable else startDrawable,
                    contentDescription =
                    if (isStarted) context.getString(R.string.stop)
                    else context.getString(R.string.resume),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
        }
    }
}