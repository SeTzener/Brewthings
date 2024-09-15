package com.brewthings.app.data.model

import com.brewthings.app.data.domain.BrewData
import com.brewthings.app.data.domain.Insight
import com.brewthings.app.data.domain.MeasurementData
import com.brewthings.app.data.domain.SensorData
import com.brewthings.app.util.datetime.TimeRange
import kotlinx.datetime.Instant

data class RaptPillInsights(
    override val timestamp: Instant,
    override val temperature: Insight,
    override val gravity: Insight,
    override val gravityVelocity: Insight?,
    override val tilt: Insight,
    override val battery: Insight,
    override val isOG: Boolean,
    override val isFG: Boolean,
    override val abv: Insight?,
    override val calculatedVelocity: Insight?,
    override val durationSinceOG: TimeRange?,
) : MeasurementData, BrewData, SensorData<Insight>
