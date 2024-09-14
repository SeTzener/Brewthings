package com.brewthings.app.data.model

import com.brewthings.app.util.floatingAngle

data class ScannedRaptPillData(
    val temperature: Float,
    val gravity: Float,
    val gravityVelocity: Float?,
    val x: Float,
    val y: Float,
    val z: Float,
    val battery: Float,
) {
    val floatingAngle: Float = floatingAngle(x, y, z)
}
