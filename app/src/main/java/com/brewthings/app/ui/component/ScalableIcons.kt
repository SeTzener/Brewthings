package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat

@Composable
fun ShrinkToFitIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    maxSize: Dp,
    tint: Color,
    contentDescription: String? = null,
) {
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

@Composable
fun ScaleToHeightIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    desiredHeight: Dp,
    tint: Color,
    contentDescription: String? = null,
) {
    val context = LocalContext.current
    val drawable = remember(iconRes) {
        ContextCompat.getDrawable(context, iconRes)
    }

    val intrinsicWidth = drawable?.intrinsicWidth ?: 1  // Avoid division by zero
    val intrinsicHeight = drawable?.intrinsicHeight ?: 1
    val aspectRatio = intrinsicWidth.toFloat() / intrinsicHeight.toFloat()

    Icon(
        modifier = modifier
            .height(desiredHeight)
            .aspectRatio(aspectRatio),
        painter = painterResource(iconRes),
        contentDescription = contentDescription,
        tint = tint,
    )
}
