package com.brewthings.app.data.repository

import com.brewthings.app.data.model.Insight
import com.brewthings.app.data.model.OGInsight
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.toModelItem
import com.brewthings.app.util.Logger
import kotlin.math.abs
import kotlinx.datetime.Instant

class RaptPillInsightsRepository(
    private val macAddress: String,
    private val dao: RaptPillDao
) {
    private val logger = Logger("RaptPillInsightsRepository")

    private val cache = mutableMapOf<Instant, RaptPillInsights>()

    suspend fun getInsights(timestamp: Instant): RaptPillInsights? {
        val cachedData = cache[timestamp]
        if (cachedData != null) return cachedData

        val ogData = dao.getOG(macAddress)?.toModelItem()
        if (ogData == null) {
            logger.error("No OG data found for $macAddress")
            return null
        }

        val data = dao.getDataAndPrevious(macAddress, timestamp)
        if (data.isEmpty()) {
            logger.error("No data found for $macAddress, $timestamp")
            return null
        }

        val pillData = data.first().toModelItem()
        val previousData = data.getOrNull(1)?.toModelItem()

        return calculateInsights(ogData, pillData, previousData).also {
            cache[timestamp] = it
        }
    }

    fun invalidateCache() {
        cache.clear()
    }

    private fun calculateInsights(
        ogData: RaptPillData,
        pillData: RaptPillData,
        previousData: RaptPillData?
    ): RaptPillInsights {
        if (pillData == ogData) {
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
            velocity = velocity?.let {
                OGInsight(
                    value = it,
                    deltaFromPrevious = previousData?.let { calculateVelocity(it, pillData) },
                )
            },
        )
    }

    private fun calculateABV(og: Float, fg: Float): Float {
        if (og <= 1.0 || fg <= 1.0) {
            logger.error("Invalid OG or FG values: og=$og, fg=$fg")
            return 0f
        }
        return (og - fg) * 131.25f
    }

    private fun calculateVelocity(ogData: RaptPillData, fgData: RaptPillData): Float? {
        val gravityDrop = fgData.gravity - ogData.gravity
        val timeDifference = (fgData.timestamp - ogData.timestamp).inWholeDays.toFloat()
        val velocity = gravityDrop / timeDifference
        return if (velocity.isInfinite() || velocity.isNaN()) {
            null
        } else velocity
    }
}
