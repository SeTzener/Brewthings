package com.brewthings.app.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun <T : Any?> FlashColorAnimation(
    backgroundColor: Color,
    targetColor: Color,
    data: T,
    content: @Composable (Color, T) -> Unit,
) {
    val animationDurationMs = 1000

    var isFirstComposition by remember { mutableStateOf(true) }
    var isFlashing by remember { mutableStateOf(false) }
    var cachedData by remember { mutableStateOf(data) }

    val animatedColor by animateColorAsState(
        targetValue = if (isFlashing) backgroundColor else targetColor,
        animationSpec = tween(durationMillis = animationDurationMs / 2),
        label = "Color Flash Animation",
    )

    LaunchedEffect(data) {
        if (!isFirstComposition) {
            isFlashing = false
            repeat(2) { // 2 transitions: target -> background, background -> target
                isFlashing = !isFlashing
                delay(animationDurationMs / 2L)
                cachedData = data // Only change data in the middle of the animation
            }
        } else {
            isFirstComposition = false
        }
    }

    content(animatedColor, cachedData)
}
