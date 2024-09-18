package com.justdeax.composeTimer.ui
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TextButton(number: String, onClick: () -> Unit) {
    var isAnimating by remember { mutableStateOf(false) }
    var cornerRadius by remember { mutableStateOf(50.dp) }

    val animatedCornerRadius by animateDpAsState(
        targetValue = cornerRadius,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            cornerRadius = 18.dp
            delay(300)
            cornerRadius = 50.dp
            delay(300)
            isAnimating = false
        }
    }

    Button(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(animatedCornerRadius),
        onClick = {
            onClick()
            isAnimating = true
        }
    ) {
        Text(
            text = number,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun IconButton(painter: Painter, contentDesc: String, onClick: () -> Unit) {
    var isAnimating by remember { mutableStateOf(false) }
    var cornerRadius by remember { mutableStateOf(50.dp) }

    val animatedCornerRadius by animateDpAsState(
        targetValue = cornerRadius,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            cornerRadius = 18.dp
            delay(300)
            cornerRadius = 50.dp
            delay(300)
            isAnimating = false
        }
    }

    Button(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(animatedCornerRadius),
        onClick = {
            onClick()
            isAnimating = true
        }
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDesc,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}