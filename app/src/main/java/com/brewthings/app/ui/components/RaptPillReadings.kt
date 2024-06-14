package com.brewthings.app.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewthings.app.R
import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.ui.theme.BrewthingsTheme
import kotlin.math.abs
import kotlinx.datetime.Instant

@Composable
fun RaptPillReadings(
    data: RaptPillData,
    deltaFromPrevious: Map<DataType, Float>,
    deltaFromOG: Map<DataType, Float>,
) {
    Card {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                RaptPillValue(
                    modifier = Modifier.weight(1f),
                    iconResId = R.drawable.ic_gravity,
                    textResId = R.string.pill_gravity,
                    value = data.gravity,
                    delta1 = deltaFromPrevious[DataType.GRAVITY],
                    delta2 = deltaFromOG[DataType.GRAVITY],
                )

                RaptPillValue(
                    modifier = Modifier.weight(1f),
                    iconResId = R.drawable.ic_tilt,
                    textResId = R.string.pill_tilt,
                    value = data.floatingAngle,
                    delta1 = deltaFromPrevious[DataType.TILT],
                    delta2 = deltaFromOG[DataType.TILT],
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                RaptPillValue(
                    modifier = Modifier.weight(1f),
                    iconResId = R.drawable.ic_temperature,
                    textResId = R.string.pill_temperature,
                    value = data.temperature,
                    delta1 = deltaFromPrevious[DataType.TEMPERATURE],
                    delta2 = deltaFromOG[DataType.TEMPERATURE],
                )

                RaptPillValue(
                    modifier = Modifier.weight(1f),
                    textWithIcon = {
                        TextWithIcon(
                            icon = { BatteryLevelIndicator(batteryPercentage = data.battery) },
                            text = stringResource(id = R.string.pill_battery, data.battery)
                        )
                    },
                    textResId = R.string.pill_battery,
                    delta1 = deltaFromPrevious[DataType.BATTERY],
                    delta2 = deltaFromOG[DataType.BATTERY],
                )
            }
        }
    }
}

@Composable
fun RaptPillValue(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    @StringRes textResId: Int,
    value: Float,
    delta1: Float? = null,
    delta2: Float? = null,
) {
    RaptPillValue(
        modifier = modifier,
        textResId = textResId,
        delta1 = delta1,
        delta2 = delta2,
    ) {
        TextWithIcon(
            iconResId = iconResId,
            text = stringResource(id = textResId, value)
        )
    }
}

@Composable
fun RaptPillValue(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    delta1: Float? = null,
    delta2: Float? = null,
    textWithIcon: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        textWithIcon()

        Spacer(modifier = Modifier.size(12.dp))

        Column(horizontalAlignment = Alignment.End) {
            DeltaText(textResId, delta1)
            DeltaText(textResId, delta2)
        }
    }
}

@Composable
fun DeltaText(
    @StringRes textResId: Int,
    delta: Float? = null,
) {
    TextWithIcon(
        iconResId = delta?.asArrowDropIcon(),
        iconSize = 12.dp,
        text = delta?.let { stringResource(id = textResId, abs(it)) } ?: "",
        iconPadding = 0.dp,
        iconColor = MaterialTheme.colorScheme.onSurface,
        textStyle = MaterialTheme.typography.bodySmall.copy(
            fontSize = 8.sp
        ),
    )
}

@DrawableRes
private fun Float.asArrowDropIcon(): Int? = when {
    this > 0 -> R.drawable.ic_arrow_drop_up
    this < 0 -> R.drawable.ic_arrow_drop_down
    else -> null
}

@Preview(apiLevel = 33) // workaround for AS Hedgehog and below
@Composable
fun RaptPillReadingsPreview() {
    BrewthingsTheme {
        RaptPillReadings(
            data = RaptPillData(
                timestamp = Instant.fromEpochMilliseconds(1716738391308L),
                temperature = 22.43f,
                gravity = 1.100f,
                x = 236.0625f,
                y = 4049.375f,
                z = 1008.9375f,
                battery = 100.0f
            ),
            deltaFromPrevious = mapOf(
                DataType.GRAVITY to -0.005f,
                DataType.TILT to -0.10f,
                DataType.BATTERY to 0f,
            ),
            deltaFromOG = mapOf(
                DataType.GRAVITY to 0.060f,
                DataType.TEMPERATURE to 5.3f,
                DataType.BATTERY to 15f,
            )
        )
    }
}
