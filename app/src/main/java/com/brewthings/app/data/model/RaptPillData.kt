package com.brewthings.app.data.model

import java.time.Instant
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

data class RaptPillData(
    val timestamp: Instant = Instant.now(),
    val temperature: Float,
    val gravity: Float,
    val x: Float,
    val y: Float,
    val z: Float,
    val battery: Float
) {
    val floatingAngle: Float = atan2(sqrt(x*x + y*y), z) * (180.0f / PI.toFloat())
}
