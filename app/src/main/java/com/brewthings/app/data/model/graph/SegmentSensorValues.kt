package com.brewthings.app.data.model.graph

import com.brewthings.app.data.model.RaptPillData
import java.time.Instant

/**
 * A list of sensor values from a given segment and sensor type.
 *
 * @param values a list of [sensor values][SegmentSensorValue].
 */
data class SegmentSensorValues(
    val values: List<SegmentSensorValue>
)

/**
 * A single set of sensor values from a given segment and instant.
 *
 * @param data
 */
data class SegmentSensorValue(
    val timestamp: Instant,
    val gravity: Float,
    val temperature: Float,
) {
    val maxValue = maxOf(gravity, temperature)
}

fun RaptPillData.asSegmentSensorValue(): SegmentSensorValue = SegmentSensorValue(
    timestamp = timestamp,
    gravity = gravity,
    temperature = temperature,
)
