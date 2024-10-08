package com.justdeax.composeTimer.ui
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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