package com.justdeax.composeTimer.ui
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisplayKeyboard(
    modifier: Modifier,
    appendEditText: (Char) -> Unit
) {
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
        }
    }
}