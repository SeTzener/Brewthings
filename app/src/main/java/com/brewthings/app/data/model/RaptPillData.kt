package com.brewthings.app.data.model

import com.brewthings.app.util.floatingAngle
import kotlinx.datetime.Instant

data class RaptPillData(
    val timestamp: Instant,
    val temperature: Float,
    val gravity: Float,
    val gravityVelocity: Float?,
    val x: Float,
    val y: Float,
    val z: Float,
    val battery: Float,
    val isOG: Boolean? = null,
    val isFG: Boolean? = null
) {
    val floatingAngle: Float = floatingAngle(x, y, z)
}
