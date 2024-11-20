package com.brewthings.app.util

import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.util.datetime.daysBetweenIgnoringTime

fun calculateABV(og: Float, fg: Float): Float {
    if (og <= 1.0 || fg <= 1.0) return 0f
    // TODO("add all the feedings to the calculation")
    // create a query to get all the feeding points and the feeding points[-1]
    // In the repository write a function to return a float with the sum of gravity deltas
    // Add the float result to the OG
    return (og - fg) * 131.25f
}

fun calculateVelocity(ogData: RaptPillData, fgData: RaptPillData): Float? {
    val gravityDrop = fgData.gravity - ogData.gravity
    val timeDifference = daysBetweenIgnoringTime(fgData.timestamp, ogData.timestamp).toFloat()
    val velocity = gravityDrop / timeDifference
    return if (velocity.isInfinite() || velocity.isNaN()) {
        null
    } else {
        velocity
    }
}
