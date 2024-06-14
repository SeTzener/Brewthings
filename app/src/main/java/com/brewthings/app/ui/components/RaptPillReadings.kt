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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewthings.app.R
import com.brewthings.app.data.model.Insight
import com.brewthings.app.data.model.OGInsight
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.util.datetime.toFormattedDate
import kotlin.math.abs
import kotlinx.datetime.Instant

@Composable
fun RaptPillReadings(data: RaptPillInsights) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = data.timestamp.toFormattedDate(),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                data.gravity.run {
                    RaptPillValue(
                        modifier = Modifier.weight(1f),
                        iconResId = R.drawable.ic_gravity,
                        textResId = R.string.pill_gravity,
                        value = value,
                        delta1 = deltaFromPrevious,
                        delta2 = deltaFromOG,
                    )
                }
                data.tilt.run {
                    RaptPillValue(
                        modifier = Modifier.weight(1f),
                        iconResId = R.drawable.ic_tilt,
                        textResId = R.string.pill_tilt,
                        value = value,
                        delta1 = deltaFromPrevious,
                        delta2 = deltaFromOG,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                data.temperature.run {
                    RaptPillValue(
                        modifier = Modifier.weight(1f),
                        iconResId = R.drawable.ic_temperature,
                        textResId = R.string.pill_temperature,
                        value = value,
                        delta1 = deltaFromPrevious,
                        delta2 = deltaFromOG,
                    )
                }
                data.battery.run {
                    RaptPillValue(
                        modifier = Modifier.weight(1f),
                        textWithIcon = {
                            TextWithIcon(
                                icon = { BatteryLevelIndicator(batteryPercentage = value) },
                                text = stringResource(id = R.string.pill_battery, value)
                            )
                        },
                        textResId = R.string.pill_battery,
                        delta1 = deltaFromPrevious,
                        delta2 = deltaFromOG,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 12.dp)
             ) {
                data.abv.run {
                    RaptPillValue(
                        modifier = Modifier.weight(1f),
                        iconResId = R.drawable.ic_abv,
                        textResId = R.string.pill_abv,
                        value = value,
                        delta1 = deltaFromPrevious,
                    )
                }
                data.velocity.run {
                    RaptPillValue(
                        modifier = Modifier.weight(1f),
                        iconResId = R.drawable.ic_velocity,
                        textResId = R.string.pill_velocity,
                        value = value,
                        delta1 = deltaFromPrevious,
                    )
                }
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
            data = RaptPillInsights(
                timestamp = Instant.fromEpochMilliseconds(1716738391308L),
                temperature = Insight(
                    value = 22.43f,
                    deltaFromOG = 5.3f,
                ),
                gravity = Insight(
                    value = 1.100f,
                    deltaFromPrevious = -0.005f,
                    deltaFromOG = 0.060f,
                ),
                battery = Insight(
                    value = 100.0f,
                    deltaFromPrevious = 0f,
                    deltaFromOG = 15f,
                ),
                tilt = Insight(
                    value = 0.5f,
                    deltaFromPrevious = -0.10f,
                ),
                abv = OGInsight(
                    value = 5.5f,
                    deltaFromPrevious = 0.5f,
                ),
                velocity = OGInsight(
                    value = 0.020f,
                    deltaFromPrevious = -0.002f,
                )
            )
        )
    }
}
