package com.brewthings.app.ui.screens.graph

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.Insight
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.ui.components.BatteryLevelIndicator
import com.brewthings.app.ui.components.ExpandableCard
import com.brewthings.app.ui.components.IconAlign
import com.brewthings.app.ui.components.TextWithIcon
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.ui.theme.Typography
import com.brewthings.app.util.datetime.TimeRange
import com.brewthings.app.util.datetime.format
import com.brewthings.app.util.datetime.toFormattedDate
import kotlin.math.abs
import kotlinx.datetime.Instant
import org.koin.androidx.compose.koinViewModel

@Composable
fun GraphInsights(
    macAddress: String,
    data: RaptPillInsights,
    dataType: DataType,
    viewModel: GraphScreenViewModel = koinViewModel(),
) {
    Card {
        ExpandableCard(
            topContent = {
                InsightsTimeHeader(data)
            },
            collapsedContent = {
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    InsightsHeader()
                    when (dataType) {
                        DataType.GRAVITY -> {
                            InsightsGravity(data.gravity)
                        }

                        DataType.TEMPERATURE -> {
                            InsightsTemperature(data.temperature)
                        }

                        DataType.BATTERY -> {
                            InsightsBattery(data.battery)
                        }
                    }
                }
            },
            expandedContent = {
                Column {
                    InsightsHeader()
                    InsightsGravity(gravity = data.gravity)
                    InsightsTemperature(temperature = data.temperature)
                    InsightsBattery(battery = data.battery)
                    InsightsTilt(tilt = data.tilt)
                    InsightsAbv(abv = data.abv)
                    InsightsCalculatedVelocity(calculatedVelocity = data.calculatedVelocity)
                    BrewStageFooter(
                        isOG = data.isOG,
                        isFG = data.isFG,
                        setIsOG = { isOG -> viewModel.setIsOG(macAddress, data.timestamp, isOG) },
                        setIsFG = { isFG -> viewModel.setIsFG(macAddress, data.timestamp, isFG) },
                    )
                }
            },
        )
    }
}

@Composable
fun InsightsHeader() {
    InsightsRow(
        icon = { Spacer(modifier = Modifier.size(24.dp)) },
        label = { InsightHeader(it, R.string.graph_header_name) },
        value = { InsightHeader(it, R.string.graph_header_value) },
        fromPrevious = { InsightHeader(it, R.string.graph_header_from_previous) },
        fromOG = { InsightHeader(it, R.string.graph_header_from_og) },
    )
}

@Composable
fun InsightsGravity(gravity: Insight) {
    InsightsRow(
        icon = { InsightIcon(it, R.drawable.ic_gravity) },
        label = { InsightLabel(it, R.string.graph_data_label_gravity) },
        value = { InsightValue(it, R.string.pill_gravity, gravity.value) },
        fromPrevious = {
            InsightDelta(
                it,
                R.string.pill_gravity,
                gravity.deltaFromPrevious
            )
        },
        fromOG = { InsightDelta(it, R.string.pill_gravity, gravity.deltaFromOG) },
    )
}

@Composable
fun InsightsTemperature(temperature: Insight) {
    InsightsRow(
        icon = { InsightIcon(it, R.drawable.ic_temperature) },
        label = { InsightLabel(it, R.string.graph_data_label_temp_short) },
        value = { InsightValue(it, R.string.pill_temperature, temperature.value) },
        fromPrevious = {
            InsightDelta(
                it,
                R.string.pill_temperature,
                temperature.deltaFromPrevious
            )
        },
        fromOG = {
            InsightDelta(
                it,
                R.string.pill_temperature,
                temperature.deltaFromOG
            )
        },
    )
}

@Composable
fun InsightsTilt(tilt: Insight) {
    InsightsRow(
        icon = { InsightIcon(it, R.drawable.ic_tilt) },
        label = { InsightLabel(it, R.string.graph_data_label_tilt) },
        value = { InsightValue(it, R.string.pill_tilt, tilt.value) },
        fromPrevious = {
            InsightDelta(
                it,
                R.string.pill_tilt,
                tilt.deltaFromPrevious
            )
        },
        fromOG = { InsightDelta(it, R.string.pill_tilt, tilt.deltaFromOG) },
    )
}

@Composable
fun InsightsBattery(battery: Insight) {
    InsightsRow(
        icon = { BatteryLevelIndicator(battery.value) },
        label = { InsightLabel(it, R.string.graph_data_label_battery) },
        value = { InsightValue(it, R.string.pill_battery, battery.value) },
        fromPrevious = {
            InsightDelta(
                it,
                R.string.pill_battery,
                battery.deltaFromPrevious
            )
        },
        fromOG = { InsightDelta(it, R.string.pill_battery, battery.deltaFromOG) },
    )
}

@Composable
fun InsightsAbv(abv: Insight?) {
    InsightsRow(
        icon = { InsightIcon(it, R.drawable.ic_abv) },
        label = { InsightLabel(it, R.string.graph_data_label_abv) },
        value = { InsightValue(it, R.string.pill_abv, abv?.value) },
        fromPrevious = { InsightDelta(it, R.string.pill_abv, abv?.deltaFromPrevious) },
    )
}

@Composable
fun InsightsCalculatedVelocity(calculatedVelocity: Insight?) {
    InsightsRow(
        icon = { InsightIcon(it, R.drawable.ic_velocity) },
        label = { InsightLabel(it, R.string.graph_data_label_velocity) },
        value = { InsightValue(it, R.string.pill_velocity, calculatedVelocity?.value) },
        fromPrevious = {
            InsightDelta(
                it,
                R.string.pill_velocity,
                calculatedVelocity?.deltaFromPrevious
            )
        },
    )
}

@Composable
fun BrewStageFooter(
    isOG: Boolean,
    isFG: Boolean,
    setIsOG: (Boolean) -> Unit,
    setIsFG: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
    ) {
        TextButton(
            modifier = Modifier.padding(start = 7.dp),
            onClick = { setIsOG(!isOG) },
        ) {
            Text(
                text = if (isOG) {
                    stringResource(id = R.string.unset_OG)
                } else {
                    stringResource(
                        id = R.string.set_OG
                    )
                },
                style = Typography.bodyMedium,
            )
        }

        TextButton(
            modifier = Modifier.padding(start = 4.dp),
            onClick = { setIsFG(!isFG) }
        ) {
            Text(
                text = if (isFG) {
                    stringResource(id = R.string.unset_FG)
                } else {
                    stringResource(
                        id = R.string.set_FG
                    )
                },
                style = Typography.bodyMedium,
            )
        }
    }
}

@Composable
fun InsightsTimeHeader(data: RaptPillInsights) {
    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InsightIcon(iconResId = R.drawable.ic_calendar)
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(
                text = data.timestamp.toFormattedDate(),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = when {
                    data.isFG && data.durationSinceOG != null -> stringResource(
                        id = R.string.graph_data_duration_fg_since_og,
                        data.durationSinceOG.format()
                    )

                    data.isOG -> stringResource(id = R.string.graph_data_duration_og)

                    data.durationSinceOG != null -> stringResource(
                        id = R.string.graph_data_duration_since_og,
                        data.durationSinceOG.format()
                    )

                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun InsightsRow(
    modifier: Modifier = Modifier,
    icon: @Composable (Modifier) -> Unit,
    label: @Composable (Modifier) -> Unit,
    value: @Composable (Modifier) -> Unit,
    fromPrevious: @Composable (Modifier) -> Unit,
    fromOG: @Composable (Modifier) -> Unit = { Spacer(it) },
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon(Modifier)
        Spacer(modifier = Modifier.size(16.dp))
        label(Modifier.weight(1f))
        Spacer(modifier = Modifier.size(8.dp))
        value(Modifier.weight(1f))
        Spacer(modifier = Modifier.size(16.dp))
        fromPrevious(Modifier.weight(1f))
        Spacer(modifier = Modifier.size(8.dp))
        fromOG(Modifier.weight(1f))
    }
}

@Composable
fun InsightHeader(
    modifier: Modifier = Modifier,
    @StringRes headerResId: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = headerResId),
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        ),
    )
}

@Composable
fun InsightIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
) {
    Icon(
        modifier = modifier.size(24.dp),
        painter = painterResource(id = iconResId),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun InsightLabel(
    modifier: Modifier = Modifier,
    @StringRes labelResId: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = labelResId),
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun InsightValue(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    value: Float?,
) {
    Text(
        modifier = modifier,
        text = if (value != null && !value.isNaN()) stringResource(id = textResId, value) else "",
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun InsightDelta(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    delta: Float?,
) {
    TextWithIcon(
        modifier = modifier,
        iconResId = delta?.asArrowDropIcon(),
        iconSize = 16.dp,
        text = if (delta != null && !delta.isNaN()) stringResource(
            id = textResId,
            abs(delta)
        ) else "",
        iconPadding = 0.dp,
        iconColor = MaterialTheme.colorScheme.onSurface,
        textStyle = MaterialTheme.typography.bodySmall,
        iconAlign = IconAlign.End,
        textAlign = TextAlign.Start,
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
    val timestamp = Instant.parse("2024-06-01T15:46:31Z")
    val timestampOG = Instant.parse("2024-05-26T00:00:00Z")
    BrewthingsTheme {
        GraphInsights(
            macAddress = "64:B7:08:58:20:B6",
            dataType = DataType.GRAVITY,
            data = RaptPillInsights(
                timestamp = timestamp,
                temperature = Insight(
                    value = 22.43f,
                    deltaFromOG = 5.3f,
                ),
                gravity = Insight(
                    value = 1.100f,
                    deltaFromPrevious = -0.005f,
                    deltaFromOG = 0.060f,
                ),
                gravityVelocity = Insight(
                    value = 0.022f,
                    deltaFromPrevious = -0.003f,
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
                abv = Insight(
                    value = 5.5f,
                    deltaFromPrevious = 0.5f,
                ),
                calculatedVelocity = Insight(
                    value = 0.020f,
                    deltaFromPrevious = -0.002f,
                ),
                durationSinceOG = TimeRange(timestampOG, timestamp),
                isOG = false,
                isFG = false
            )
        )
    }
}
