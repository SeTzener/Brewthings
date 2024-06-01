package com.brewthings.app.data.model.graph

import java.time.Duration
import java.time.Instant

/**
 * The state of SensorValuesGraph.
 *
 * @param graphTimeRange The entire time range that the graph should display.
 * @param visibleTimePeriod The time period in seconds that the graph should display without requiring panning.
 * @param dateFormat The format to use when formatting timestamps. Used for x axis label rendering.
 * @param maxValueTimeGap The maximal amount of time between two sensor values for them to be drawn in a single line.
 * @param sensorValues The sensor values. Can be null if the data for the current graph time span has not yet loaded.
 */
data class DeviceSensorGraphState(
    val graphTimeRange: ClosedRange<Instant>,
    val visibleTimePeriod: Long,
    val dateFormat: String,
    val maxValueTimeGap: Duration,
    val sensorValues: SegmentSensorValues? = null,
)
