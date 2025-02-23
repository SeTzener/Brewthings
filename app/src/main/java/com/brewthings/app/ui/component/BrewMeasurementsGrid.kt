package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@Composable
fun BrewMeasurementsGrid(
    modifier: Modifier = Modifier,
    data: BrewMeasurements,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { BrewTimeCard(data.timeRange) }
        items(data.measurements) { BrewMeasurementCard(it) }
    }
}

@Composable
fun BrewMeasurementCard(measurement: Measurement) {
    val unit = measurement.dataType.toUnit()
    val formatter = measurement.dataType.toValueFormatter()
    BrewCard(
        headerIconResId = measurement.dataType.toIconRes(measurement.value),
        header = measurement.dataType.toLabel(),
        content = { modifier, textColor ->
            ValueRow(
                modifier = modifier,
                textColor = textColor,
                formattedValue = formatter.format(measurement.value),
                unit = unit,
                trendIconRes = measurement.trend.toIconRes(),
            )
        },
        footer = measurement.previousValue?.let {
            stringResource(R.string.sensor_measurement_footer, formatter.format(it), unit)
        } ?: "",
    )
}

@Composable
fun BrewTimeCard(range: TimeRange) {
    BrewCard(
        headerIconResId = R.drawable.ic_calendar,
        header = stringResource(R.string.brew_time_header),
        content = { modifier, textColor ->
            TimeRow(
                modifier = modifier,
                textColor = textColor,
                formattedTime = range.format()
            )
        },
        footer = stringResource(
            R.string.brew_time_footer,
            range.from.toSimpleFormattedDate()
        ),
    )
}

@Composable
private fun BrewCard(
    @DrawableRes headerIconResId: Int,
    header: String,
    content: @Composable (Modifier, Color) -> Unit,
    footer: String,
) {
    Card {
        val verticalPadding = 10.dp
        val textColor = MaterialTheme.colorScheme.onSurface
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
        textStyle = MaterialTheme.typography.bodyMedium,
        iconPadding = 4.dp,
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
            style = MaterialTheme.typography.titleLarge,
        )

        Text(
            modifier = Modifier
                .constrainAs(unitRef) {
                    baseline.linkTo(valueRef.baseline)
                    start.linkTo(valueRef.end)
                }
                .padding(start = interHorizontalPadding),
            text = unit,
            color = textColor,
            style = MaterialTheme.typography.titleSmall,
        )

        if (trendIconRes != null) {
            ScalableIcon(
                modifier = Modifier
                    .constrainAs(trendIconRef) {
                        bottom.linkTo(valueRef.bottom)
                        start.linkTo(unitRef.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = interHorizontalPadding),
                painter = painterResource(id = trendIconRes),
                tint = textColor,
                maxSize = 24.dp
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
        style = MaterialTheme.typography.titleLarge,
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
                    Measurement(DataType.ABV, 1.20f, 1.09f)
                ),
            ),
        )
    }
}
