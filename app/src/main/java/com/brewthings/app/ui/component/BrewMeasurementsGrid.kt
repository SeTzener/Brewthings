package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.brewthings.app.R
import com.brewthings.app.data.domain.BrewMeasurements
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.domain.Measurement
import com.brewthings.app.ui.converter.toIconRes
import com.brewthings.app.ui.converter.toLabel
import com.brewthings.app.ui.converter.toUnit
import com.brewthings.app.ui.converter.toValueFormatter
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.util.datetime.TimeRange
import com.brewthings.app.util.datetime.format
import com.brewthings.app.util.datetime.toSimpleFormattedDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days

@Composable
fun BrewMeasurementsGrid(
    modifier: Modifier = Modifier,
    data: BrewMeasurements,
    isLastBrew: Boolean = false,
    onTimeClick: (() -> Unit)? = null,
    onClick: ((DataType) -> Unit)? = null,
) {
    VerticalGrid(
        modifier = modifier,
        columnsCount = 2,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        items = listOf(GridItem.TimeRange(data.timeRange)) +
            data.measurements.map { measurement ->
                GridItem.Measurement(measurement)
            },
    ) { item ->
        when (item) {
            is GridItem.TimeRange -> BrewTimeCard(item.data, isLastBrew, onTimeClick)
            is GridItem.Measurement -> BrewMeasurementCard(item.data, isLastBrew, onClick)
        }
    }
}

@Composable
fun BrewMeasurementCard(
    measurement: Measurement,
    isLastBrew: Boolean = false,
    onClick: ((DataType) -> Unit)? = null,
) {
    val unit = measurement.dataType.toUnit()
    val formatter = measurement.dataType.toValueFormatter()
    val dataType = measurement.dataType
    val header = if (isLastBrew && dataType == DataType.ABV) {
        stringResource(R.string.last_brew_abv_header)
    } else {
        dataType.toLabel()
    }
    val backgroundColor = CardDefaults.cardColors().containerColor

    FlashColorAnimation(
        backgroundColor = backgroundColor,
        targetColor = MaterialTheme.colorScheme.onSurface,
        data = measurement,
    ) { textColor, data ->
        val value = data.value
        val headerIconResId = dataType.toIconRes(value)
        val trendIconRes = data.trend.toIconRes()
        val previousValue = data.previousValue

        BrewCard(
            backgroundColor = backgroundColor,
            textColor = textColor,
            headerIconResId = headerIconResId,
            header = header,
            content = { modifier, parentColor ->
                ValueRow(
                    modifier = modifier,
                    textColor = parentColor,
                    formattedValue = formatter.format(value),
                    unit = unit,
                    trendIconRes = trendIconRes,
                )
            },
            footer = previousValue?.let {
                stringResource(R.string.sensor_measurement_footer, formatter.format(it), unit)
            } ?: "",
            onClick = onClick?.let { { it(dataType) } },
        )
    }
}

@Composable
fun BrewTimeCard(range: TimeRange, isLastBrew: Boolean = false, onClick: (() -> Unit)? = null) {
    val backgroundColor = CardDefaults.cardColors().containerColor
    val from = range.from.toSimpleFormattedDate()

    FlashColorAnimation(
        backgroundColor = backgroundColor,
        targetColor = MaterialTheme.colorScheme.onSurface,
        data = range.format(),
    ) { textColor, data ->
        BrewCard(
            backgroundColor = backgroundColor,
            textColor = textColor,
            headerIconResId = R.drawable.ic_calendar,
            header = stringResource(
                if (isLastBrew) R.string.last_brew_time_header else R.string.brew_time_header,
            ),
            content = { modifier, parentColor ->
                TimeRow(
                    modifier = modifier,
                    textColor = parentColor,
                    formattedTime = data,
                )
            },
            footer = stringResource(
                if (isLastBrew) R.string.last_brew_time_footer else R.string.brew_time_footer,
                from,
            ),
            onClick = onClick,
        )
    }
}

@Composable
private fun BrewCard(
    backgroundColor: Color,
    textColor: Color,
    @DrawableRes headerIconResId: Int,
    header: String,
    content: @Composable (Modifier, Color) -> Unit,
    footer: String,
    onClick: (() -> Unit)? = null,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
        onClick = onClick ?: {},
        enabled = onClick != null,
    ) {
        val verticalPadding = 10.dp
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderRow(
                textColor = textColor,
                headerIconResId = headerIconResId,
                header = header,
            )

            content(
                Modifier
                    .fillMaxWidth()
                    .padding(top = verticalPadding),
                textColor,
            )

            FooterRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = verticalPadding),
                textColor = textColor,
                footer = footer,
            )
        }
    }
}

@Composable
private fun HeaderRow(
    modifier: Modifier = Modifier,
    textColor: Color,
    @DrawableRes headerIconResId: Int,
    header: String,
) {
    TextWithIcon(
        modifier = modifier,
        iconResId = headerIconResId,
        text = header,
        textColor = textColor,
        textStyle = MaterialTheme.typography.bodySmall,
        iconPadding = 4.dp,
        iconSize = 18.dp,
        iconColor = textColor,
    )
}

@Composable
private fun ValueRow(
    modifier: Modifier = Modifier,
    textColor: Color,
    formattedValue: String,
    unit: String,
    @DrawableRes trendIconRes: Int?,
) {
    ConstraintLayout(modifier) {
        val (valueRef, unitRef, trendIconRef) = createRefs()
        val interHorizontalPadding = 8.dp
        Text(
            modifier = Modifier
                .constrainAs(valueRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            text = formattedValue,
            color = textColor,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
        )

        Text(
            modifier = Modifier
                .constrainAs(unitRef) {
                    baseline.linkTo(valueRef.baseline)
                    start.linkTo(valueRef.end)
                }
                .padding(start = interHorizontalPadding / 2),
            text = unit,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
        )

        if (trendIconRes != null) {
            ShrinkToFitIcon(
                modifier = Modifier
                    .constrainAs(trendIconRef) {
                        bottom.linkTo(valueRef.bottom)
                        start.linkTo(unitRef.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = interHorizontalPadding),
                iconRes = trendIconRes,
                tint = textColor,
                maxSize = 24.dp,
            )
        }
    }
}

@Composable
private fun TimeRow(
    modifier: Modifier = Modifier,
    textColor: Color,
    formattedTime: String,
) {
    Text(
        modifier = modifier,
        text = formattedTime,
        color = textColor,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
    )
}

@Composable
private fun FooterRow(
    modifier: Modifier = Modifier,
    textColor: Color,
    footer: String,
) {
    Text(
        modifier = modifier,
        text = footer,
        color = textColor,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Preview
@Composable
fun BrewMeasurementsGridPreview() {
    BrewthingsTheme {
        val startDate = LocalDate(2024, 12, 26).atStartOfDayIn(TimeZone.UTC)
        BrewMeasurementsGrid(
            modifier = Modifier.width(360.dp),
            data = BrewMeasurements(
                timeRange = TimeRange(
                    from = startDate,
                    to = startDate + 6.days,
                ),
                measurements = listOf(
                    Measurement(DataType.ABV, 1.20f, 1.09f),
                ),
            ),
        )
    }
}

private sealed interface GridItem {
    data class TimeRange(val data: com.brewthings.app.util.datetime.TimeRange) : GridItem
    data class Measurement(val data: com.brewthings.app.data.domain.Measurement) : GridItem
}
