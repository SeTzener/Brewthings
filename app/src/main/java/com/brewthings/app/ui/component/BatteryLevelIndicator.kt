package com.brewthings.app.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.ui.converter.toIconDrawable

@Composable
fun BatteryLevelIndicator(
    batteryPercentage: Float,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Icon(
        imageVector = ImageVector.vectorResource(
            id = DataType.BATTERY.toIconDrawable(batteryPercentage)
        ),
        contentDescription = null,
        tint = tint,
    )
}
