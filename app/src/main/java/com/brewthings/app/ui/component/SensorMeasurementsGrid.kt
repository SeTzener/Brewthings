package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.domain.Measurement
import com.brewthings.app.data.domain.SensorMeasurements
import com.brewthings.app.ui.converter.toColor
import com.brewthings.app.ui.converter.toIconRes
import com.brewthings.app.ui.converter.toLabel
import com.brewthings.app.ui.converter.toUnit
import com.brewthings.app.ui.converter.toValueFormatter
import com.brewthings.app.ui.theme.BrewthingsTheme

@Composable
fun SensorMeasurementsGrid(
    modifier: Modifier = Modifier,
    measurements: SensorMeasurements,
) {
    VerticalGrid(
        modifier = modifier,
        columnsCount = 2,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        items = measurements,
    ) {
        SensorMeasurementCard(it)
    }
}

@Composable
private fun SensorMeasurementCard(measurement: Measurement) {
    val dataType = measurement.dataType
    val unit = dataType.toUnit()
    val header = dataType.toLabel()
    val formatter = dataType.toValueFormatter()
    val backgroundColor = measurement.dataType.toColor(isDarkTheme = isSystemInDarkTheme())

    FlashColorAnimation(
        backgroundColor = backgroundColor,
        targetColor = Color.White,
        data = measurement,
    ) { textColor, data ->
        val previousValue = data.previousValue
        val value = data.value
        val headerIconRes = dataType.toIconRes(value = value, trimmed = true)
        val trendIconRes = data.trend.toIconRes()

        SensorMeasurementCard(
            backgroundColor = backgroundColor,
            textColor = textColor,
            headerIconRes = headerIconRes,
            header = header,
            formattedValue = formatter.format(value),
            unit = unit,
            trendIconRes = trendIconRes,
            footer = previousValue?.let {
                stringResource(R.string.sensor_measurement_footer, formatter.format(it), unit)
            } ?: "",
        )
    }
}

@Composable
fun SensorMeasurementCard(
    backgroundColor: Color,
    textColor: Color,
    @DrawableRes headerIconRes: Int,
    header: String,
    formattedValue: String,
    unit: String,
    @DrawableRes trendIconRes: Int?,
    footer: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
    ) {
        val verticalPadding = 16.dp
        val horizontalPadding = 16.dp
        val interVertical = 6.dp
        val interHorizontalPadding = 8.dp

        ConstraintLayout(modifier = Modifier.padding(vertical = verticalPadding)) {
            val (headerIconRef, headerRef, valueRef, unitRef, trendIconRef, footerRef) = createRefs()

            ScaleToHeightIcon(
                modifier = Modifier
                    .constrainAs(headerIconRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(horizontal = horizontalPadding),
                iconRes = headerIconRes,
                desiredHeight = 32.dp,
                contentDescription = null,
                tint = textColor,
            )

            Text(
                modifier = Modifier
                    .constrainAs(headerRef) {
                        top.linkTo(headerIconRef.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = interVertical * 1.5f,
                    )
                    .fillMaxWidth(),
                text = header,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
            )

            Text(
                modifier = Modifier
                    .constrainAs(valueRef) {
                        top.linkTo(headerRef.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(
                        start = horizontalPadding,
                        top = interVertical,
                    ),
                text = formattedValue,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                color = textColor,
            )

            Text(
                modifier = Modifier
                    .constrainAs(unitRef) {
                        baseline.linkTo(valueRef.baseline)
                        start.linkTo(valueRef.end)
                    }
                    .padding(start = interHorizontalPadding / 2),
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
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
                        .padding(
                            start = interHorizontalPadding,
                            top = interVertical,
                            end = horizontalPadding,
                        ),
                    iconRes = trendIconRes,
                    contentDescription = null,
                    tint = textColor,
                    maxSize = 24.dp,
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(footerRef) {
                        top.linkTo(valueRef.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = interVertical,
                    )
                    .fillMaxWidth(),
                text = footer,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
            )
        }
    }
}

@Preview
@Composable
fun SensorMeasurementGridPreview() {
    BrewthingsTheme {
        SensorMeasurementsGrid(
            modifier = Modifier.width(360.dp),
            measurements = listOf(
                Measurement(DataType.GRAVITY, 1.056f, 1.060f),
                Measurement(DataType.TEMPERATURE, 18.25f, 18.25f),
                Measurement(DataType.BATTERY, 0.58f, 0.5825f),
                Measurement(DataType.VELOCITY_MEASURED, 1.575f, 0.617f),
            ),
        )
    }
}
