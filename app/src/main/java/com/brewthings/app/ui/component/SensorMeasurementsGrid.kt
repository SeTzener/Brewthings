package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        items = measurements,
    ) {
        SensorMeasurementCard(it)
    }
}

@Composable
private fun SensorMeasurementCard(measurement: Measurement) {
    val (dataType, value, previousValue) = measurement
    val unit = dataType.toUnit()
    val formatter = dataType.toValueFormatter()
    SensorMeasurementCard(
        backgroundColor = dataType.toColor(),
        textColor = Color.White,
        headerIconRes = dataType.toIconRes(value),
        header = dataType.toLabel(),
        formattedValue = formatter.format(value),
        unit = unit,
        trendIconRes = measurement.trend.toIconRes(),
        footer = previousValue?.let {
            stringResource(R.string.sensor_measurement_footer, formatter.format(it), unit)
        } ?: ""
    )
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
        val interVertical = 4.dp
        val interHorizontalPadding = 8.dp

        ConstraintLayout(modifier = Modifier.padding(vertical = verticalPadding)) {
            val (headerIconRef, headerRef, valueRef, unitRef, trendIconRef, footerRef) = createRefs()

            Icon(
                modifier = Modifier
                    .constrainAs(headerIconRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(start = 12.dp) // TODO(walt): replace icons, then remove this
                    .size(56.dp),
                painter = painterResource(id = headerIconRes),
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
                        top = interVertical,
                    )
                    .fillMaxWidth(),
                text = header,
                style = MaterialTheme.typography.titleMedium,
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
                style = MaterialTheme.typography.headlineMedium,
                color = textColor,
            )

            Text(
                modifier = Modifier
                    .constrainAs(unitRef) {
                        baseline.linkTo(valueRef.baseline)
                        start.linkTo(valueRef.end)
                    }
                    .padding(start = interHorizontalPadding),
                text = unit,
                style = MaterialTheme.typography.titleSmall,
                color = textColor,
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
                        .padding(
                            start = interHorizontalPadding,
                            top = interVertical,
                            end = horizontalPadding,
                        ),
                    painter = painterResource(id = trendIconRes),
                    contentDescription = null,
                    tint = textColor,
                    maxSize = 36.dp
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
                style = MaterialTheme.typography.bodyMedium,
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
                Measurement(DataType.VELOCITY_MEASURED, 1.575f, 0.617f),
                Measurement(DataType.BATTERY, 0.58f, 0.5825f),
            )
        )
    }
}
