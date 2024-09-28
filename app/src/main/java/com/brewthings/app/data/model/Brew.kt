package com.brewthings.app.data.model

import com.brewthings.app.util.calculateABV
import com.brewthings.app.util.datetime.TimeRange
import kotlinx.serialization.Serializable

@Serializable
data class Brew (
    val og: RaptPillData,
    val fgOrLast: RaptPillData,
    val isCompleted: Boolean,
    val durationSinceOG: TimeRange = TimeRange(og.timestamp, fgOrLast.timestamp),
    val abv: Float = calculateABV(og.gravity, fgOrLast.gravity)
)


