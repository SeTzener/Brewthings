package com.brewthings.app.ui.components

import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.brewthings.app.R
import com.brewthings.app.ui.theme.Red_Alert

@Composable
fun BatteryLevelIndicator(
    batteryPercentage: Float,
    colorPrimary: Color = MaterialTheme.colorScheme.primary,
    colorAlert: Color = Red_Alert,
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

    val batteryTint = if (batteryPercentage <= 0.375f) colorAlert else colorPrimary

    Icon(
        imageVector = ImageVector.vectorResource(id = batteryAsset),
        contentDescription = null,
        tint = batteryTint,
    )
}
