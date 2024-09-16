package com.justdeax.composeTimer.ui
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OutlineIconButton(modifier: Modifier, onClick: () -> Unit, painter: Painter, contentDesc: String) {
    Box {
        OutlinedButton(
            modifier = modifier
                .width(90.dp)
                .height(60.dp)
                .padding(5.dp),
            onClick = onClick,
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

fun doSame(number: Byte) {
    Log.d("TAG", "doSame: $number")
}

@Composable
fun TextButton(number: Byte) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.size(85.dp),
            onClick = { doSame(number) }
        ) {
            Text(
                text = number.toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun IconButton(width: Int = 85, onClick: () -> Unit, painter: Painter, contentDesc: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .width(width.dp)
                .height(85.dp),
            onClick = onClick
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun IconButton2(height: Int, onClick: () -> Unit, painter: Painter, contentDesc: String) {
    Button(
        modifier = Modifier
            .height(height.dp)
            .width(70.dp),
        onClick = onClick
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDesc,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}