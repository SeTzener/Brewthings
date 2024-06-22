package com.brewthings.app.ui.screens.graph

import com.brewthings.app.data.model.Insight
import com.brewthings.app.data.model.OGInsight
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.util.datetime.TimeRange
import com.brewthings.app.util.datetime.daysBetweenIgnoringTime
import kotlin.math.abs

fun List<RaptPillData>.toInsights(ogData: RaptPillData?): List<RaptPillInsights> =
    mapIndexed { index, raptPillData ->
        val previousData = if (index > 0) get(index - 1) else null
        calculateInsights(ogData, raptPillData, previousData)
    }

private fun calculateInsights(
    ogData: RaptPillData?,
    pillData: RaptPillData,
    previousData: RaptPillData?
): RaptPillInsights {
    if (ogData == null || pillData == ogData) {
        return RaptPillInsights(
            timestamp = pillData.timestamp,
            temperature = Insight(value = pillData.temperature),
            gravity = Insight(value = pillData.gravity),
            battery = Insight(value = pillData.battery),
            tilt = Insight(value = pillData.floatingAngle),
        )
    }

    val abv = calculateABV(ogData.gravity, pillData.gravity)
    val velocity = calculateVelocity(ogData, pillData)?.let { abs(it) }
    return RaptPillInsights(
        timestamp = pillData.timestamp,
        temperature = Insight(
            value = pillData.temperature,
            deltaFromPrevious = previousData?.let { pillData.temperature - it.temperature },
            deltaFromOG = pillData.temperature - ogData.temperature,
        ),
        gravity = Insight(
            value = pillData.gravity,
            deltaFromPrevious = previousData?.let { pillData.gravity - it.gravity },
            deltaFromOG = pillData.gravity - ogData.gravity,
        ),
        battery = Insight(
            value = pillData.battery,
            deltaFromPrevious = previousData?.let { pillData.battery - it.battery },
            deltaFromOG = pillData.battery - ogData.battery,
        ),
        tilt = Insight(
            value = pillData.floatingAngle,
            deltaFromPrevious = previousData?.let { pillData.floatingAngle - it.floatingAngle },
            deltaFromOG = pillData.floatingAngle - ogData.floatingAngle,
        ),
        abv = OGInsight(
            value = abv,
            deltaFromPrevious = previousData?.let { abv - calculateABV(ogData.gravity, it.gravity) },
        ),
        velocity = velocity?.let { value ->
            OGInsight(
                value = value,
                deltaFromPrevious = previousData?.let { calculateVelocity(it, pillData) },
            )
        },
        durationFromOG = TimeRange(ogData.timestamp, pillData.timestamp),
    )
}

private fun calculateABV(og: Float, fg: Float): Float {
    if (og <= 1.0 || fg <= 1.0) return 0f
    return (og - fg) * 131.25f
}

private fun calculateVelocity(ogData: RaptPillData, fgData: RaptPillData): Float? {
    val gravityDrop = fgData.gravity - ogData.gravity
    val timeDifference = daysBetweenIgnoringTime(fgData.timestamp, ogData.timestamp).toFloat()
    val velocity = gravityDrop / timeDifference
    return if (velocity.isInfinite() || velocity.isNaN()) {
        null
    } else velocity
}
