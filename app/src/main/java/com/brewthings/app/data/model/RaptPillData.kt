package com.brewthings.app.data.model

import com.brewthings.app.data.domain.BrewStage
import com.brewthings.app.data.domain.SensorWithTiltReadings
import kotlinx.datetime.Instant

data class RaptPillData(
    override val timestamp: Instant,
    override val temperature: Float,
    override val gravity: Float,
    override val gravityVelocity: Float?,
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val battery: Float,
    override val isOG: Boolean,
    override val isFG: Boolean,
) : SensorWithTiltReadings, BrewStage
