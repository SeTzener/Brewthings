package com.brewthings.app.data.repository

import com.brewthings.app.data.model.Brew
import com.brewthings.app.data.model.Brews
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillReadings
import com.brewthings.app.data.storage.toModelItem
import com.brewthings.app.util.calculateFeeding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class BrewsRepository(
    private val dao: RaptPillDao,
) {
    suspend fun observeBrews(): Flow<List<Brews>> = dao.observePills().map { pills ->
        pills.mapNotNull { pill ->
            getBrews(pill.macAddress)
                .takeIf { it.isNotEmpty() }
                ?.let {
                    Brews(
                        batchName = pill.name ?: pill.macAddress,
                        data = it,
                    )
                }
        }
    }

    private suspend fun getBrews(macAddress: String): List<Brew> {
        val brews: MutableList<Brew> = mutableListOf()

        // Collect the first list of edges from the flow
        val edges = dao.getBrewEdges(macAddress)
        if (edges.isEmpty()) {
            return emptyList()
        }

        // Determine the last measurement: either the last FG or fallback to the last measurement from the database
        val lastMeasurement: RaptPillReadings = if (edges.last().readings.isFG == true) {
            edges.last().readings
        } else {
            dao.getLastMeasurement(macAddress).first().readings
        }

        var currentOg: RaptPillData? = null
        var lastFg: RaptPillData? = null

        for (measurement in edges) {
            when {
                measurement.readings.isOG == true -> {
                    if (currentOg == null) {
                        // Set the first OG found
                        currentOg = measurement.toModelItem()
                    } else {
                        // Add an incomplete Brew if an OG is followed by another OG
                        brews.add(
                            createBrew(
                                macAddress = macAddress,
                                start = currentOg,
                                end = lastFg ?: measurement.toModelItem(),
                                isCompleted = false,
                            ),
                        )
                        // Update currentOg to the latest OG
                        currentOg = measurement.toModelItem()
                        lastFg = null
                    }
                }

                measurement.readings.isFG == true -> {
                    if (currentOg != null) {
                        // Complete the Brew when FG follows OG
                        brews.add(
                            createBrew(
                                macAddress = macAddress,
                                start = currentOg,
                                end = measurement.toModelItem(),
                                isCompleted = true,
                            ),
                        )
                        lastFg = measurement.toModelItem() // Update lastFg to the current FG
                        currentOg = null // Reset currentOg after completion
                    } else if (lastFg != null) {
                        // Create incomplete Brew if FG appears consecutively
                        brews.add(
                            createBrew(
                                macAddress = macAddress,
                                start = lastFg,
                                end = measurement.toModelItem(),
                                isCompleted = false,
                            ),
                        )
                        lastFg = measurement.toModelItem() // Update lastFg to the current FG
                    } else {
                        // Create incomplete Brew if FG appears without a preceding OG
                        val firstMeasurement = dao.getFirstMeasurement(macAddress).first()
                        brews.add(
                            createBrew(
                                macAddress = macAddress,
                                start = firstMeasurement.toModelItem(),
                                end = measurement.toModelItem(),
                                isCompleted = false,
                            ),
                        )
                        lastFg = measurement.toModelItem() // Update lastFg to the current FG
                    }
                }
            }
        }

        // If there's an uncompleted OG, pair it with the last measurement
        if (currentOg != null) {
            brews.add(
                Brew(
                    og = currentOg,
                    feedings = getFeedingsAndDiluting(
                        macAddress = macAddress,
                        startDate = currentOg.timestamp,
                        endDate = lastMeasurement.toModelItem().timestamp,
                    ),
                    fgOrLast = lastMeasurement.toModelItem(),
                    isCompleted = false,
                ),
            )
        }

        return brews
    }

    private suspend fun getFeedingsAndDiluting(
        macAddress: String,
        startDate: Instant,
        endDate: Instant,
    ): List<Float> {
        val data = dao.getBrewData(macAddress, startDate, endDate)
        var previousGravity: Float? = null
        val result = mutableListOf<Float>()

        for (item in data) {
            if (previousGravity == 0f) {
                previousGravity = item.readings.gravity
                continue
            }

            if (item.readings.isFeeding == true && previousGravity != null) {
                result += calculateFeeding(previousGravity, item.readings.gravity)
            }

            previousGravity = item.readings.gravity
        }

        return result
    }

    private suspend fun createBrew(macAddress: String, start: RaptPillData, end: RaptPillData, isCompleted: Boolean) =
        Brew(
            og = start,
            feedings = getFeedingsAndDiluting(
                macAddress = macAddress,
                startDate = start.timestamp,
                endDate = end.timestamp,
            ),
            fgOrLast = end,
            isCompleted = isCompleted,
        )
}
