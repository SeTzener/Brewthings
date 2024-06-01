package com.brewthings.app.data.model.graph

import com.brewthings.app.data.model.RaptPillData
import java.time.Instant

/**
 * A list of data points from a given segment.
 *
 * @param values a list of [GraphDataPoint].
 */
data class GraphDataSegment(
    val values: List<GraphDataPoint>
)

/**
 * A single data point from a given segment and instant.
 *
 * @param data The data to show in the overview.
 */
data class GraphDataPoint(val data: RaptPillData) {
    val timestamp: Instant get() = data.timestamp
    val gravity: Float get() = data.gravity
    val temperature: Float get() = data.temperature

    val maxValue = maxOf(gravity, temperature)
}
