package com.brewthings.app.data.repository

import com.brewthings.app.data.model.Insight
import com.brewthings.app.data.model.OGInsight
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.toModelItem
import com.brewthings.app.util.Logger
import kotlin.math.abs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class RaptPillInsightsRepository(
    private val macAddress: String,
    private val dao: RaptPillDao
) {
    private val logger = Logger("RaptPillInsightsRepository")
    private val cache = mutableMapOf<Instant, RaptPillInsights>()
    private val selectedTimestamp: MutableStateFlow<Instant?> = MutableStateFlow(null)

    val selectedInsights: Flow<RaptPillInsights?> = dao.observeOG(macAddress)
        .map { it?.toModelItem() }
        .onEach { invalidateCache() }
        .flatMapLatest { ogData ->
            selectedTimestamp.flatMapLatest { timestamp ->
                if (timestamp == null) {
                    flowOf(null)
                } else {
                    dao.observeDataAndPrevious(macAddress, timestamp)
                        .map { list -> list.map { it.toModelItem() } }
                        .map { data -> getInsights(timestamp, ogData, data) }
                }
            }
        }

    suspend fun setTimestamp(timestamp: Instant?) {
        selectedTimestamp.emit(timestamp)
    }

    private fun getInsights(timestamp: Instant, ogData: RaptPillData?, data: List<RaptPillData>): RaptPillInsights? {
        val cachedData = cache[timestamp]
        if (cachedData != null) {
            logger.info("Fetching cached insights for $timestamp.")
            return cachedData
        }

        if (data.isEmpty()) {
            logger.error("No data found for $macAddress, $timestamp")
            return null
        }

        val pillData = data.first()
        val previousData = data.getOrNull(1)

        return calculateInsights(ogData, pillData, previousData).also {
            cache[timestamp] = it
        }
    }

    private fun invalidateCache() {
        logger.info("Invalidating cache.")
        cache.clear()
    }

    private fun calculateInsights(
        ogData: RaptPillData?,
        pillData: RaptPillData,
        previousData: RaptPillData?
    ): RaptPillInsights {
        logger.info(
            "Calculating insights for for ${pillData.timestamp}.\n" +
                    "PillData: $pillData.\n" +
                    "Previous: $previousData\n" +
                    "OG: $ogData"
        )
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
            durationFromOG = pillData.timestamp - ogData.timestamp,
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
