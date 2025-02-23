package com.brewthings.app.data.domain

import com.brewthings.app.data.domain.Trend.Downwards
import com.brewthings.app.data.domain.Trend.Stationary
import com.brewthings.app.data.domain.Trend.Upwards
import com.brewthings.app.util.datetime.TimeRange

typealias SensorMeasurements = List<Measurement>

data class BrewMeasurements(
    val timeRange: TimeRange,
    val measurements: List<Measurement>,
)

data class Measurement(
    val dataType: DataType,
    val value: Float,
    val previousValue: Float?,
) {
    val trend: Trend = when {
        previousValue == null -> Stationary
        value - previousValue > 0 -> Upwards
        value - previousValue < 0 -> Downwards
        else -> Stationary
    }
}
