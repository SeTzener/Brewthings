package com.brewthings.app.data.domain

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

interface SensorWithTiltData : SensorData<Float>, TiltData {
    override val tilt: Float get() = atan2(sqrt(x * x + y * y), z) * (180.0f / PI.toFloat())
}
