package com.justdeax.composeTimer.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justdeax.composeTimer.R
import com.justdeax.composeTimer.util.TimerState2
import com.justdeax.composeTimer.util.toFormatString

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