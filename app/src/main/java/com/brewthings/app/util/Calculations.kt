package com.brewthings.app.util

import com.brewthings.app.data.model.RaptPillData

fun calculateFeeding(previousGravity: Float, actualGravity: Float): Float =
    actualGravity.minus(previousGravity)

fun calculateABV(og: Float, fg: Float, feedings: List<Float>): Float? {
    if (og <= 1.0 || fg <= 1.0) return null
    return (og.sumAll(feedings) - fg) * 131.25f
}

fun calculateVelocity(previous: RaptPillData?, fg: RaptPillData): Float? {
    if (previous == null) return null

    val gpDrop = (fg.gravity - previous.gravity) * 1000f
    val daysBetween = (fg.timestamp.epochSeconds - previous.timestamp.epochSeconds) / 86_400f
    return (gpDrop / daysBetween).sanitizeVelocity()
}

fun Float.sanitizeVelocity(): Float? =
    if (isInfinite() || isNaN() || this < -100 || this > 100) {
        null // Invalid velocity.
    } else {
        -1 * this // Invert the sign, to make it more intuitive.
    }
