package com.brewthings.app.data.model

import com.brewthings.app.util.datetime.TimeRange
import kotlinx.datetime.Instant

data class RaptPillInsights(
    val timestamp: Instant,
    val temperature: Insight,
    val gravity: Insight,
    val tilt: Insight,
    val battery: Insight,
    val abv: OGInsight? = null,
    val velocity: OGInsight? = null,
    val durationFromOG: TimeRange? = null,
)

data class Insight(
    val value: Float,
    val deltaFromPrevious: Float? = null,
    val deltaFromOG: Float? = null,
)

data class OGInsight(
    val value: Float,
    val deltaFromPrevious: Float? = null,
    val isOG: Boolean?,
    val isFG: Boolean?
)
