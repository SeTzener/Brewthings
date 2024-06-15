package com.brewthings.app.ui.screens.graph

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.model.Insight
import com.brewthings.app.data.model.OGInsight
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.ui.components.BatteryLevelIndicator
import com.brewthings.app.ui.components.TextWithIcon
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.util.datetime.toFormattedDate
import kotlin.math.abs
import kotlinx.datetime.Instant

@Composable
fun GraphInsights(data: RaptPillInsights) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = data.timestamp.toFormattedDate(),
                style = MaterialTheme.typography.bodyMedium,
            )

            InsightsRow(
                textWithIcon = {
                    InsightHeader(it.padding(start = 30.dp), R.string.graph_header_name, TextAlign.Start)
                },
                value = { InsightHeader(it, R.string.graph_header_value) },
                fromPrevious = { InsightHeader(it.padding(end = 2.dp), R.string.graph_header_from_previous) },
                fromOG = { InsightHeader(it.padding(end = 2.dp), R.string.graph_header_from_og) },
            )

            InsightsRow(
                textWithIcon = { InsightTextWithIcon(it, R.drawable.ic_gravity, R.string.graph_data_label_gravity) },
                value = { InsightValue(it, R.string.pill_gravity, data.gravity.value) },
                fromPrevious = { InsightDelta(it, R.string.pill_gravity, data.gravity.deltaFromPrevious) },
                fromOG = { InsightDelta(it, R.string.pill_gravity, data.gravity.deltaFromOG) },
            )

            InsightsRow(
                textWithIcon = {
                    InsightTextWithIcon(it, R.drawable.ic_temperature, R.string.graph_data_label_temperature)
                },
                value = { InsightValue(it, R.string.pill_temperature, data.temperature.value) },
                fromPrevious = { InsightDelta(it, R.string.pill_temperature, data.temperature.deltaFromPrevious) },
                fromOG = { InsightDelta(it, R.string.pill_temperature, data.temperature.deltaFromOG) },
            )

            InsightsRow(
                textWithIcon = { InsightTextWithIcon(it, R.drawable.ic_tilt, R.string.graph_data_label_tilt) },
                value = { InsightValue(it, R.string.pill_tilt, data.tilt.value) },
                fromPrevious = { InsightDelta(it, R.string.pill_tilt, data.tilt.deltaFromPrevious) },
                fromOG = { InsightDelta(it, R.string.pill_tilt, data.tilt.deltaFromOG) },
            )

            InsightsRow(
                textWithIcon = { InsightBattery(it, data.battery.value) },
                value = { InsightValue(it, R.string.pill_battery, data.battery.value) },
                fromPrevious = { InsightDelta(it, R.string.pill_battery, data.battery.deltaFromPrevious) },
                fromOG = { InsightDelta(it, R.string.pill_battery, data.battery.deltaFromOG) },
            )

            data.abv?.also { abv ->
                InsightsRow(
                    textWithIcon = { InsightTextWithIcon(it, R.drawable.ic_abv, R.string.graph_data_label_abv) },
                    value = { InsightValue(it, R.string.pill_abv, abv.value) },
                    fromPrevious = { InsightDelta(it, R.string.pill_abv, abv.deltaFromPrevious) },
                )
            }

            data.velocity?.also { velocity ->
                InsightsRow(
                    textWithIcon = {
                        InsightTextWithIcon(it, R.drawable.ic_velocity, R.string.graph_data_label_velocity)
                    },
                    value = { InsightValue(it, R.string.pill_velocity, velocity.value) },
                    fromPrevious = { InsightDelta(it, R.string.pill_velocity, velocity.deltaFromPrevious) },
                )
            }
        }
    }
}

@Composable
fun InsightsRow(
    modifier: Modifier = Modifier,
    textWithIcon: @Composable (Modifier) -> Unit,
    value: @Composable (Modifier) -> Unit,
    fromPrevious: @Composable (Modifier) -> Unit,
    fromOG: @Composable (Modifier) -> Unit = { Spacer(it) },
) {
    Row(
        modifier = modifier.padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        textWithIcon(Modifier.weight(1.7f))
        value(Modifier.weight(1f))
        Spacer(modifier = Modifier.size(16.dp))
        fromPrevious(Modifier.weight(1f))
        fromOG(Modifier.weight(1f))
    }
}

@Composable
fun InsightHeader(
    modifier: Modifier,
    @StringRes headerResId: Int,
    textAlign: TextAlign = TextAlign.End,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = headerResId),
        textAlign = textAlign,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Black
        ),
    )
}

@Composable
fun InsightTextWithIcon(
    modifier: Modifier,
    @DrawableRes iconResId: Int,
    @StringRes labelResId: Int,
) {
    TextWithIcon(
        modifier = modifier,
        iconResId = iconResId,
        iconPadding = 4.dp,
        text = stringResource(id = labelResId),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun InsightBattery(
    modifier: Modifier,
    batteryPercentage: Float,
) {
    TextWithIcon(
        modifier = modifier,
        icon = { BatteryLevelIndicator(batteryPercentage) },
        iconPadding = 4.dp,
        text = stringResource(id = R.string.graph_data_label_battery),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun InsightValue(
    modifier: Modifier,
    @StringRes textResId: Int,
    value: Float,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = textResId, value),
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        ),
    )
}

@Composable
fun InsightDelta(
    modifier: Modifier,
    @StringRes textResId: Int,
    delta: Float?,
) {
    TextWithIcon(
        modifier = modifier,
        iconResId = delta?.asArrowDropIcon(),
        iconSize = 16.dp,
        text = if (delta != null) stringResource(id = textResId, abs(delta)) else "",
        iconPadding = 0.dp,
        iconColor = MaterialTheme.colorScheme.onSurface,
        textStyle = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.End,
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
fun GraphInsightsPreview() {
    BrewthingsTheme {
        GraphInsights(
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
