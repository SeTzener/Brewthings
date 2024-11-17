package com.brewthings.app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.brewthings.app.R

@Composable
fun BatteryLevelIndicator(
    batteryPercentage: Float,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val batteryAsset = when {
        batteryPercentage <= 0f -> R.drawable.ic_battery_0
        batteryPercentage <= 0.125f -> R.drawable.ic_battery_1
        batteryPercentage <= 0.25f -> R.drawable.ic_battery_2
        batteryPercentage <= 0.375f -> R.drawable.ic_battery_3
        batteryPercentage <= 0.5f -> R.drawable.ic_battery_4
        batteryPercentage <= 0.75f -> R.drawable.ic_battery_5
        batteryPercentage < 1f -> R.drawable.ic_battery_6
        else -> R.drawable.ic_battery_7
    }

    Icon(
        imageVector = ImageVector.vectorResource(id = batteryAsset),
        contentDescription = null,
        tint = tint,
    )
}
