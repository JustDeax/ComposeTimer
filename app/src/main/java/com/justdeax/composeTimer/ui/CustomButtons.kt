package com.justdeax.composeTimer.ui
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun OutlineIconButton(modifier: Modifier, painter: Painter, contentDesc: String, onClick: () -> Unit) {
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

@Composable
fun BaseButton(width: Int = 100, height: Int = 100, onClick: () -> Unit, content: @Composable (RowScope.() -> Unit)) {
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
            .width(width.dp)
            .height(height.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(animatedCornerRadius),
        onClick = {
            onClick()
            isAnimating = true
        },
        content = content
    )
}

@Composable
fun IconButton(painter: Painter, contentDesc: String, onClick: () -> Unit) {
    BaseButton(100, 85, onClick) {
        Icon(
            painter = painter,
            contentDescription = contentDesc,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}