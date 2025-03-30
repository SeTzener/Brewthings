package com.brewthings.app.util

import com.brewthings.app.data.model.RaptPillData

fun calculateFeeding(previousGravity: Float, actualGravity: Float): Float =
    actualGravity.minus(previousGravity)

fun calculateABV(og: Float, fg: Float, feedings: List<Float>): Float? {
    if (og <= 1.0 || fg <= 1.0) return null
    return (og.sumAll(feedings) - fg) * 131.25f
}

fun calculateVelocity(previousData: RaptPillData?, fgData: RaptPillData): Float? {
    if (previousData == null) return null

    val gpDrop = (fgData.gravity - previousData.gravity) * 1000f
    val daysBetween = (fgData.timestamp.epochSeconds - previousData.timestamp.epochSeconds).toFloat() / 86_400f
    val velocity = gpDrop / daysBetween
    if (velocity.validateVelocity()) return velocity else return null
}

fun Float.validateVelocity(): Boolean = isInfinite() || isNaN() || this > 0 || this < -100

fun Float.invertVelocity(): Float = -this