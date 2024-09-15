package com.brewthings.app.data.model

import com.brewthings.app.data.domain.MeasurementData
import com.brewthings.app.data.domain.SensorWithTiltData
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
) : MeasurementData, SensorWithTiltData
