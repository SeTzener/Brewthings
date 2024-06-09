package com.brewthings.app.data.model

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class RaptPillData(
    val timestamp: Instant = Clock.System.now(),
    val temperature: Float,
    val gravity: Float,
    val x: Float,
    val y: Float,
    val z: Float,
    val battery: Float
) {
    val floatingAngle: Float = atan2(sqrt(x * x + y * y), z) * (180.0f / PI.toFloat())
}
