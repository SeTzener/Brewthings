package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
fun ShrinkToFitIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    maxSize: Dp,
    tint: Color,
    contentDescription: String? = null,
) {
    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(modifier = modifier) {
        // Calculate the available space (take the smallest of width and height)
        val availableSpace = minOf(maxWidth, maxHeight)

        // Set the size based on available space but respect maxSize
        val iconSize = minOf(availableSpace, maxSize)

        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
