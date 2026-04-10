package com.milkyway.rotating

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

/**
 * A composable that continuously rotates its [content] at a constant speed.
 *
 * @param modifier Modifier applied to the rotating container.
 * @param durationMillis Duration of one full 360° rotation in milliseconds. Defaults to 2000ms.
 * @param content The composable content to rotate.
 */
@Composable
fun RotatingBox(
    modifier: Modifier = Modifier,
    durationMillis: Int = 2000,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RotatingBox")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotationAngle",
    )

    Box(
        modifier = modifier.rotate(rotation),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
