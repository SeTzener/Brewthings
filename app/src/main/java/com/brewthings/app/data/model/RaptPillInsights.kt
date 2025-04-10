package com.brewthings.app.data.model

import com.brewthings.app.data.domain.BrewInsights
import com.brewthings.app.data.domain.BrewStage
import com.brewthings.app.data.domain.Insight
import com.brewthings.app.data.domain.SensorInsights
import com.brewthings.app.util.datetime.TimeRange
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RaptPillInsights(
    override val timestamp: Instant,
    override val temperature: Insight,
    override val gravity: Insight,
    override val gravityVelocity: Insight?,
    override val tilt: Insight,
    override val battery: Insight,
    override val abv: Insight?,
    override val calculatedVelocity: Insight?,
    override val durationSinceOG: TimeRange?,
    override val isOG: Boolean,
    override val isFG: Boolean,
    override val isFeeding: Boolean,
) : SensorInsights, BrewInsights, BrewStage
