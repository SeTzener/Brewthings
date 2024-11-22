package com.brewthings.app.ui.screens.pill.insights

import com.brewthings.app.data.domain.Insight
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.data.storage.sanitizeVelocity
import com.brewthings.app.util.datetime.TimeRange

fun List<RaptPillData>.toInsights(): List<RaptPillInsights> {
    val insights = mutableListOf<RaptPillInsights>()
    var ogData: RaptPillData? = null
    var previousData: RaptPillData? = null
    var feeding = 0.0f
    var previousFeeding = feeding

    for (data in this) {
        if (data.isFeeding) {
            feeding += calculateFeeding(previousData?.gravity, data.gravity)
        }
        // Add the insights for the current data point.
        insights.add(data.toInsights(ogData = ogData, previousData = previousData, feeding = feeding, previousFeeding = previousFeeding))

        if (data.isFG) {
            // Invalidate the OG data for the next data point.
            ogData = null
        }

        if (data.isOG) {
            // Remember the OG data for the next data point.
            ogData = data
        }

        previousData = data
        previousFeeding = feeding
    }

    return insights
}

private fun RaptPillData.toInsights(
    ogData: RaptPillData?,
    previousData: RaptPillData?,
    feeding: Float,
    previousFeeding: Float,
): RaptPillInsights {
    val pillData = this
    if (ogData == null || pillData == ogData) {
        return RaptPillInsights(
            timestamp = pillData.timestamp,
            temperature = Insight(value = pillData.temperature),
            gravity = Insight(value = pillData.gravity),
            gravityVelocity = pillData.gravityVelocity?.let { Insight(value = it) },
            battery = Insight(value = pillData.battery),
            tilt = Insight(value = pillData.tilt),
            isOG = pillData.isOG,
            isFG = pillData.isFG,
            isFeeding = pillData.isFeeding,
            durationSinceOG = null,
            calculatedVelocity = null,
            abv = null,
        )
    }

    val abv = calculateABV(ogData.gravity, pillData.gravity.minus(feeding))
    val velocity = calculateVelocity(previousData, pillData)
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
            value = pillData.tilt,
            deltaFromPrevious = previousData?.let { pillData.tilt - it.tilt },
            deltaFromOG = pillData.tilt - ogData.tilt,
        ),
        abv = Insight(
            value = abv,
            deltaFromPrevious = previousData?.let { abv - calculateABV(ogData.gravity, it.gravity.minus(previousFeeding)) },
        ),
        gravityVelocity = pillData.gravityVelocity?.let { value ->
            Insight(
                value = value,
                deltaFromPrevious = previousData?.gravityVelocity?.let { pillData.gravityVelocity - it },
                deltaFromOG = ogData.gravityVelocity?.let { pillData.gravityVelocity - it },
            )
        },
        calculatedVelocity = velocity?.let { value ->
            Insight(
                value = value,
                deltaFromPrevious = previousData?.let { calculateVelocity(it, pillData) },
            )
        },
        durationSinceOG = TimeRange(ogData.timestamp, pillData.timestamp),
        isOG = pillData.isOG,
        isFG = pillData.isFG,
        isFeeding = pillData.isFeeding,
    )
}

private fun calculateFeeding(previousGravity: Float?, actualGravity: Float): Float {
    if (previousGravity == null) {
        return 0.0f
    }
    return actualGravity.minus(previousGravity)
}

private fun calculateABV(og: Float, fg: Float): Float {
    if (og <= 1.0 || fg <= 1.0) return 0f
    return (og - fg) * 131.25f
}

// This can be improved by using a more sophisticated algorithm.
private fun calculateVelocity(previousData: RaptPillData?, fgData: RaptPillData): Float? {
    if (previousData == null) return null

    val gpDrop = (fgData.gravity - previousData.gravity) * 1000f
    val daysBetween = (fgData.timestamp.epochSeconds - previousData.timestamp.epochSeconds).toFloat() / 86_400f
    val velocity = gpDrop / daysBetween
    return velocity.sanitizeVelocity()
}
