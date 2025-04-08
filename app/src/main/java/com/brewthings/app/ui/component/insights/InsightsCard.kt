package com.brewthings.app.ui.component.insights

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.domain.Insight
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.ui.component.BatteryLevelIndicator
import com.brewthings.app.ui.component.IconAlign
import com.brewthings.app.ui.component.TextWithIcon
import com.brewthings.app.ui.converter.toLineColor
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.ui.theme.Typography
import com.brewthings.app.util.datetime.TimeRange
import com.brewthings.app.util.datetime.format
import com.brewthings.app.util.datetime.toFormattedDate
import kotlinx.datetime.Instant
import kotlin.math.abs

@Composable
fun InsightsCard(
    dataTypes: List<DataType>,
    data: RaptPillInsights,
    feedings: List<Instant>,
    showActions: Boolean,
    setIsOG: (Instant, Boolean) -> Unit,
    setIsFG: (Instant, Boolean) -> Unit,
    setFeeding: (Instant, Boolean) -> Unit,
    deleteMeasurement: (Instant) -> Unit,
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            InsightsTimeHeader(data)

            InsightsHeaderRow()

            InsightsValueRow(
                isHighlighted = dataTypes.contains(DataType.GRAVITY),
                icon = {
                    InsightIcon(
                        modifier = it,
                        iconResId = R.drawable.ic_gravity,
                        tint = highlightIconColor(dataTypes, DataType.GRAVITY),
                    )
                },
                labelResId = R.string.graph_data_label_gravity,
                valueFormatResId = R.string.pill_gravity,
                insight = data.gravity,
            )

            InsightsValueRow(
                isHighlighted = dataTypes.contains(DataType.TEMPERATURE),
                icon = {
                    InsightIcon(
                        modifier = it,
                        iconResId = R.drawable.ic_temperature,
                        tint = highlightIconColor(dataTypes, DataType.TEMPERATURE),
                    )
                },
                labelResId = R.string.graph_data_label_temp_short,
                valueFormatResId = R.string.pill_temperature,
                insight = data.temperature,
            )

            InsightsValueRow(
                isHighlighted = dataTypes.contains(DataType.BATTERY),
                icon = {
                    BatteryLevelIndicator(
                        batteryPercentage = data.battery.value,
                        tint = highlightIconColor(dataTypes, DataType.BATTERY),
                    )
                },
                labelResId = R.string.graph_data_label_battery,
                valueFormatResId = R.string.pill_battery,
                insight = data.battery,
            )

            InsightsValueRow(
                isHighlighted = dataTypes.contains(DataType.TILT),
                icon = {
                    InsightIcon(
                        modifier = it,
                        iconResId = R.drawable.ic_tilt,
                        tint = highlightIconColor(dataTypes, DataType.TILT),
                    )
                },
                labelResId = R.string.graph_data_label_tilt,
                valueFormatResId = R.string.pill_tilt,
                insight = data.tilt,
            )

            InsightsValueRow(
                isHighlighted = dataTypes.contains(DataType.ABV),
                icon = {
                    InsightIcon(
                        modifier = it,
                        iconResId = R.drawable.ic_abv,
                        tint = highlightIconColor(dataTypes, DataType.ABV),
                    )
                },
                labelResId = R.string.graph_data_label_abv,
                valueFormatResId = R.string.pill_abv,
                insight = data.abv,
            )

            InsightsValueRow(
                isHighlighted = dataTypes.contains(DataType.VELOCITY_MEASURED),
                icon = {
                    InsightIcon(
                        modifier = it,
                        iconResId = R.drawable.ic_velocity_measured,
                        tint = highlightIconColor(dataTypes, DataType.VELOCITY_MEASURED),
                    )
                },
                labelResId = R.string.graph_data_label_velocity_measured_short,
                valueFormatResId = R.string.pill_velocity,
                insight = data.gravityVelocity,
            )

            InsightsValueRow(
                isHighlighted = dataTypes.contains(DataType.VELOCITY_COMPUTED),
                icon = {
                    InsightIcon(
                        modifier = it,
                        iconResId = R.drawable.ic_velocity_computed,
                        tint = highlightIconColor(dataTypes, DataType.VELOCITY_COMPUTED),
                    )
                },
                labelResId = R.string.graph_data_label_velocity_computed_short,
                valueFormatResId = R.string.pill_velocity,
                insight = data.calculatedVelocity,
            )
        }
        if (showActions) {
            InsightsActionRow(
                data = data,
                feedings = feedings,
                setIsOG = setIsOG,
                setIsFG = setIsFG,
                setFeeding = setFeeding,
                deleteMeasurement = deleteMeasurement
            )
        }
    }
}

@Composable
fun InsightsActionRow(
    data: RaptPillInsights,
    feedings: List<Instant>,
    setIsOG: (Instant, Boolean) -> Unit,
    setIsFG: (Instant, Boolean) -> Unit,
    setFeeding: (Instant, Boolean) -> Unit,
    deleteMeasurement: (Instant) -> Unit
) {
    var isDeleteMeasurement = remember { mutableStateOf(false) }
    if (isDeleteMeasurement.value) {
        deleteMeasurementDialog(
            isDeleteMeasurement = isDeleteMeasurement,
            timestamp = data.timestamp,
            deleteMeasurement = deleteMeasurement
        )
    }
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
    ) {
        TextButton(
            modifier = Modifier.padding(start = 7.dp),
            onClick = { setIsOG(data.timestamp, !data.isOG) },
        ) {
            Text(
                text = if (data.isOG) {
                    stringResource(id = R.string.unset_OG)
                } else {
                    stringResource(
                        id = R.string.set_OG,
                    )
                },
                style = Typography.bodyMedium,
            )
        }
        TextButton(
            modifier = Modifier.padding(start = 4.dp),
            onClick = { setFeeding(data.timestamp, !data.isFeeding) },
        ) {
            Text(
                text = if (feedings.contains(data.timestamp)) {
                    if (data.isFeeding) {
                        stringResource(id = R.string.unfeeding)
                    } else {
                        stringResource(id = R.string.feeding)
                    }
                } else {
                    if (data.isFeeding) {
                        stringResource(id = R.string.undiluting)
                    } else {
                        stringResource(id = R.string.diluting)
                    }
                },
                style = Typography.bodyMedium,
            )
        }
        TextButton(
            modifier = Modifier.padding(start = 4.dp),
            onClick = { setIsFG(data.timestamp, !data.isFG) },
        ) {
            Text(
                text = if (data.isFG) {
                    stringResource(id = R.string.unset_FG)
                } else {
                    stringResource(
                        id = R.string.set_FG,
                    )
                },
                style = Typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier.padding(end = 12.dp),
            onClick = { isDeleteMeasurement.value = true },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color.Red,
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun deleteMeasurementDialog(
    isDeleteMeasurement: MutableState<Boolean>,
    timestamp: Instant,
    deleteMeasurement: (Instant) -> Unit
) {
    AlertDialog(
        onDismissRequest = { isDeleteMeasurement.value = false },
        title = { Text(text = stringResource(id = R.string.delete_measurement)) },
        text = { Text(text = stringResource(id = R.string.delete_measurement_confirmation)) },
        confirmButton = {
            TextButton(
                onClick = {
                    isDeleteMeasurement.value = false
                    deleteMeasurement(timestamp)
                },
            ) {
                Text(text = stringResource(id = R.string.button_yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { isDeleteMeasurement.value = false },
            ) {
                Text(text = stringResource(id = R.string.button_no))
            }
        }
    )
}

@Composable
fun InsightsTimeHeader(data: RaptPillInsights) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
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
                text = data.getInfoText(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun RaptPillInsights.getInfoText(): String {
    val timeText = when {
        isFG && durationSinceOG != null -> stringResource(
            id = R.string.graph_data_duration_fg_since_og,
            durationSinceOG.format(),
        )

        isOG -> stringResource(id = R.string.graph_data_duration_og)

        durationSinceOG != null -> stringResource(
            id = R.string.graph_data_duration_since_og,
            durationSinceOG.format(),
        )

        else -> ""
    }

    val feedingText = when {
        isFeeding -> stringResource(id = R.string.feeding)
        isFG -> stringResource(id = R.string.diluting)
        else -> ""
    }

    return when {
        timeText.isNotEmpty() && feedingText.isNotEmpty() -> "$timeText, $feedingText"
        timeText.isNotEmpty() -> timeText
        feedingText.isNotEmpty() -> feedingText
        else -> ""
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
        modifier = modifier.padding(top = 16.dp),
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
fun InsightsHeaderRow(
    modifier: Modifier = Modifier,
) {
    InsightsRow(
        modifier = modifier,
        icon = { Spacer(modifier = Modifier.size(24.dp)) },
        label = { InsightHeader(it, R.string.graph_header_name) },
        value = { InsightHeader(it, R.string.graph_header_value) },
        fromPrevious = { InsightHeader(it, R.string.graph_header_from_previous) },
        fromOG = { InsightHeader(it, R.string.graph_header_from_og) },
    )
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
            fontWeight = FontWeight.Bold,
        ),
    )
}

@Composable
fun InsightsValueRow(
    modifier: Modifier = Modifier,
    isHighlighted: Boolean,
    icon: @Composable (Modifier) -> Unit,
    @StringRes labelResId: Int,
    @StringRes valueFormatResId: Int,
    insight: Insight?,
) {
    InsightsRow(
        modifier = modifier,
        icon = icon,
        label = {
            InsightLabel(
                modifier = it,
                isHighlighted = isHighlighted,
                labelResId = labelResId,
            )
        },
        value = {
            if (insight?.value != null) {
                InsightValue(
                    modifier = it,
                    isHighlighted = isHighlighted,
                    textResId = valueFormatResId,
                    value = insight.value,
                )
            } else {
                Spacer(it)
            }
        },
        fromPrevious = {
            if (insight?.deltaFromPrevious != null) {
                InsightDelta(
                    modifier = it,
                    isHighlighted = isHighlighted,
                    textResId = valueFormatResId,
                    delta = insight.deltaFromPrevious,
                )
            } else {
                Spacer(it)
            }
        },
        fromOG = {
            if (insight?.deltaFromOG != null) {
                InsightDelta(
                    modifier = it,
                    isHighlighted = isHighlighted,
                    textResId = valueFormatResId,
                    delta = insight.deltaFromOG,
                )
            } else {
                Spacer(it)
            }
        },
    )
}

@Composable
fun InsightIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Icon(
        modifier = modifier.size(24.dp),
        painter = painterResource(id = iconResId),
        contentDescription = null,
        tint = tint,
    )
}

@Composable
fun TextStyle.highlight(isHighlighted: Boolean): TextStyle =
    if (isHighlighted) copy(fontWeight = FontWeight.Bold) else this

@Composable
fun highlightColor(
    isHighlighted: Boolean,
    normalColor: Color = MaterialTheme.colorScheme.onSurface,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
): Color = if (isHighlighted) highlightColor else normalColor

@Composable
fun highlightIconColor(
    dataTypes: List<DataType>,
    dataType: DataType,
): Color = highlightColor(
    isHighlighted = dataTypes.contains(dataType),
    normalColor = MaterialTheme.colorScheme.primary,
    highlightColor = dataType.toLineColor(),
)

@Composable
fun InsightLabel(
    modifier: Modifier = Modifier,
    isHighlighted: Boolean,
    @StringRes labelResId: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = labelResId),
        style = MaterialTheme.typography.bodyMedium.highlight(isHighlighted),
        color = highlightColor(isHighlighted),
    )
}

@Composable
fun InsightValue(
    modifier: Modifier = Modifier,
    isHighlighted: Boolean,
    @StringRes textResId: Int,
    value: Float?,
) {
    Text(
        modifier = modifier,
        text = if (value != null && !value.isNaN()) stringResource(id = textResId, value) else "",
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.bodyMedium.highlight(isHighlighted),
        color = highlightColor(isHighlighted),
    )
}

@Composable
fun InsightDelta(
    modifier: Modifier = Modifier,
    isHighlighted: Boolean,
    @StringRes textResId: Int,
    delta: Float?,
) {
    TextWithIcon(
        modifier = modifier,
        iconResId = delta?.asArrowDropIcon(),
        iconSize = 16.dp,
        text = if (delta != null && !delta.isNaN()) {
            stringResource(
                id = textResId,
                abs(delta),
            )
        } else {
            ""
        },
        iconPadding = 0.dp,
        iconColor = highlightColor(isHighlighted),
        textStyle = MaterialTheme.typography.bodySmall.highlight(isHighlighted),
        textColor = highlightColor(isHighlighted),
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

@Preview
@Composable
fun InsightsCardPreview() {
    val timestamp = Instant.parse("2024-06-01T15:46:31Z")
    val timestampOG = Instant.parse("2024-05-26T00:00:00Z")
    BrewthingsTheme {
        InsightsCard(
            dataTypes = listOf(DataType.GRAVITY),
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
                isFG = false,
                isFeeding = true,
            ),
            feedings = emptyList(),
            showActions = true,
            setIsOG = { _, _ -> },
            setIsFG = { _, _ -> },
            setFeeding = { _, _ -> },
            deleteMeasurement = { _ -> },
        )
    }
}
