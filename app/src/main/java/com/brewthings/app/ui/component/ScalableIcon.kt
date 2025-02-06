package com.brewthings.app.ui.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp

@Composable
fun ScalableIcon(
    modifier: Modifier = Modifier,
    maxSize: Dp,
    painter: Painter,
    tint: Color,
    contentDescription: String? = null,
) {
    BoxWithConstraints(modifier = modifier) {
        // Calculate the available space (take the smallest of width and height)
        val availableSpace = minOf(maxWidth, maxHeight)

        // Set the size based on available space but respect maxSize
        val iconSize = minOf(availableSpace, maxSize)

        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}
