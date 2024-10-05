package com.justdeax.composeTimer.util
import java.util.Locale

fun Long.toFormatString(): String {
    val seconds = this % 60
    val minutes = this / 60 % 60
    val hours = this / 60 / 60
    return if (hours != 0L)
        String.format(Locale.US, "%01d:%02d:%02d", hours, minutes, seconds)
    else
        String.format(Locale.US, "%01d:%02d", minutes, seconds)
}

//fun Context.commandService(serviceState: StopwatchAction) {
//    val intent = Intent(this, StopwatchService::class.java)
//    intent.action = serviceState.name
//    this.startService(intent)
//}

enum class TimerAction {
    START_RESUME, PAUSE, RESET
}

data class TimerState(
    val timerDuration: Long,
    val startTime: Long,
    val isRunning: Boolean
)

data class TimerState2(
    val timerDuration: Long,
    val remainingTime: Long,
    val isStarted: Boolean,
    val isRunning: Boolean,
    val name: String
)