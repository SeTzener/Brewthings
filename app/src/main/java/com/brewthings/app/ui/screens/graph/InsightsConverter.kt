package com.brewthings.app.ui.screens.graph

import com.brewthings.app.data.model.Insight
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.util.datetime.TimeRange
import com.brewthings.app.util.datetime.daysBetweenIgnoringTime
import kotlin.math.abs

fun List<RaptPillData>.toInsights(): List<List<RaptPillInsights>> {
    val result = mutableListOf<List<RaptPillInsights>>()
    var currentGroup = mutableListOf<RaptPillInsights>()
    var ogData: RaptPillData? = null
    var previousData: RaptPillData? = null

    for (data in this) {
        ogData = when {
            data.isOG == true -> data
            data.isFG == true -> null
            else -> ogData
        }

        // Add the current data to the current group
        currentGroup.add(data.toInsights(ogData = ogData, previousData = previousData))

        // If it's an OG or FG, check if we need to finalize the current group
        if (data.isOG == true || data.isFG == true) {
            // If we have reached a marker (OG or FG), finalize the current group and start a new one
            result.add(currentGroup)
            currentGroup = mutableListOf()  // Start a new group
        }

        previousData = data
    }

    // Add any remaining data that hasn't been closed off by an FG
    if (currentGroup.isNotEmpty()) {
        result.add(currentGroup)
    }

    return result
}

private fun RaptPillData.toInsights(
    ogData: RaptPillData?,
    previousData: RaptPillData?
): RaptPillInsights {
    val pillData = this
    if (ogData == null || pillData == ogData) {
        return RaptPillInsights(
            timestamp = pillData.timestamp,
            temperature = Insight(value = pillData.temperature),
            gravity = Insight(value = pillData.gravity),
            battery = Insight(value = pillData.battery),
            tilt = Insight(value = pillData.floatingAngle),
            isOG = pillData.isOG ?: false,
            isFG = pillData.isFG ?: false
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
        abv = Insight(
            value = abv,
            deltaFromPrevious = previousData?.let { abv - calculateABV(ogData.gravity, it.gravity) },
        ),
        velocity = velocity?.let { value ->
            Insight(
                value = value,
                deltaFromPrevious = previousData?.let { calculateVelocity(it, pillData) },
            )
        },
        durationSinceOG = TimeRange(ogData.timestamp, pillData.timestamp),
        isOG = pillData.isOG ?: false,
        isFG = pillData.isFG ?: false
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
