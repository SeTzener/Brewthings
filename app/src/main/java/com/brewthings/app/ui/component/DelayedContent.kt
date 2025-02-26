package com.brewthings.app.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun DelayedContent(
    delayMillis: Duration = 1.seconds,
    content: @Composable () -> Unit,
) {
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        isReady = true
    }

    if (isReady) {
        content()
    }
}
