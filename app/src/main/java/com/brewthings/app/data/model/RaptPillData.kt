package com.brewthings.app.data.model

import com.brewthings.app.data.domain.BrewStage
import com.brewthings.app.data.domain.SensorWithTiltReadings
import com.brewthings.app.util.sanitizeVelocity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RaptPillData(
    override val timestamp: Instant,
    override val temperature: Float,
    override val gravity: Float,
    val rawVelocity: Float?,
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val battery: Float,
    override val isOG: Boolean,
    override val isFG: Boolean,
    override val isFeeding: Boolean,
) : SensorWithTiltReadings, BrewStage {
    override val gravityVelocity: Float? = rawVelocity?.sanitizeVelocity()
}
