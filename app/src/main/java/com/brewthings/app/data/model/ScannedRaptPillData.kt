package com.brewthings.app.data.model

import com.brewthings.app.data.domain.SensorWithTiltReadings
import com.brewthings.app.util.sanitizeVelocity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ScannedRaptPillData(
    override val timestamp: Instant,
    override val temperature: Float,
    override val gravity: Float,
    val rawVelocity: Float?,
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val battery: Float,
) : SensorWithTiltReadings {
    override val gravityVelocity: Float? = rawVelocity?.sanitizeVelocity()
}
