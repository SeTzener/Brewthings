package com.brewthings.app.ui.screens.pill.insights

import com.brewthings.app.data.domain.Insight
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.util.calculateABV
import com.brewthings.app.util.calculateFeeding
import com.brewthings.app.util.calculateVelocity
import com.brewthings.app.util.datetime.TimeRange

fun List<RaptPillData>.toInsights(): List<RaptPillInsights> {
    val insights = mutableListOf<RaptPillInsights>()
    var ogData: RaptPillData? = null
    var previousData: RaptPillData? = null
    var previousFeedings = mutableListOf<Float>()

    for (data in this) {
        val feeding = if (data.isFeeding && previousData != null) {
            calculateFeeding(previousData.gravity, data.gravity)
        } else null

        // Add the insights for the current data point.
        insights.add(
            data.toInsights(
                ogData = ogData,
                previousData = previousData,
                currentFeeding = feeding,
                previousFeedings = previousFeedings,
            )
        )

        if (data.isFG) {
            // Invalidate the OG data for the next data point.
            ogData = null
        }

        if (data.isOG) {
            // Remember the OG data for the next data point.
            ogData = data
            previousFeedings = mutableListOf()
        }

        previousData = data
        feeding?.also {
            previousFeedings.add(it)
        }
    }

    return insights
}

private fun RaptPillData.toInsights(
    ogData: RaptPillData?,
    previousData: RaptPillData?,
    currentFeeding: Float?,
    previousFeedings: List<Float>,
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

    val feedings = currentFeeding?.let { previousFeedings + it } ?: previousFeedings
    val abv = calculateABV(ogData.gravity, pillData.gravity, feedings)

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
        abv = abv?.let { value ->
            Insight(
                value = value,
                deltaFromPrevious = previousData?.let {
                    calculateABV(it.gravity, pillData.gravity, previousFeedings)
                },
            )
        },
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
