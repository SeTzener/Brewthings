package com.brewthings.app.util

import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.util.datetime.daysBetweenIgnoringTime

fun calculateABV(og: Float, fg: Float): Float {
    if (og <= 1.0 || fg <= 1.0) return 0f
    return (og - fg) * 131.25f
}

fun calculateVelocity(ogData: RaptPillData, fgData: RaptPillData): Float? {
    val gravityDrop = fgData.gravity - ogData.gravity
    val timeDifference = daysBetweenIgnoringTime(fgData.timestamp, ogData.timestamp).toFloat()
    val velocity = gravityDrop / timeDifference
    return if (velocity.isInfinite() || velocity.isNaN()) {
        null
    } else velocity
}