package com.brewthings.app.data.repository

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.model.Brew
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillReadings
import com.brewthings.app.data.storage.toDaoItem
import com.brewthings.app.data.storage.toModelItem
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder.Graph.macAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant

class RaptPillRepository(
    private val scanner: RaptPillScanner,
    private val dao: RaptPillDao
) {
    fun fromBluetooth(): Flow<ScannedRaptPill> = scanner.scan()

    fun fromDatabase(): Flow<List<RaptPill>> = dao.observeAll().map { query ->
        query.map { db ->
            RaptPill(
                macAddress = db.pill.macAddress,
                name = db.pill.name,
                data = db.data.map { data ->
                    data.toModelItem()
                }
            )
        }
    }

    suspend fun getBrews(macAddress: String): List<Brew> {
        val brews: MutableList<Brew> = mutableListOf()

        // Collect the first list of edges from the flow
        val edges = dao.getBrewEdges(macAddress).first()

        // If edges is empty, return an empty list
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
                        brews.add(Brew(og = currentOg, fgOrLast = lastFg ?: measurement.toModelItem(), isCompleted = false))
                        // Update currentOg to the latest OG
                        currentOg = measurement.toModelItem()
                        lastFg = null
                    }
                }

                measurement.readings.isFG == true -> {
                    if (currentOg != null) {
                        // Complete the Brew when FG follows OG
                        brews.add(Brew(og = currentOg, fgOrLast = measurement.toModelItem(), isCompleted = true))
                        lastFg = measurement.toModelItem() // Update lastFg to the current FG
                        currentOg = null // Reset currentOg after completion
                    } else if (lastFg != null) {
                        // Create incomplete Brew if FG appears consecutively
                        brews.add(Brew(og = lastFg!!, fgOrLast = measurement.toModelItem(), isCompleted = false))
                        lastFg = measurement.toModelItem() // Update lastFg to the current FG
                    } else {
                        // Create incomplete Brew if FG appears without a preceding OG
                        val firstMeasurement = dao.getFirstMeasurement(macAddress).first()
                        brews.add(Brew(og = firstMeasurement.toModelItem(), fgOrLast = measurement.toModelItem(), isCompleted = false))
                        lastFg = measurement.toModelItem() // Update lastFg to the current FG
                    }
                }
            }
        }

        // If there's an uncompleted OG, pair it with the last measurement
        if (currentOg != null) {
            brews.add(Brew(og = currentOg, fgOrLast = lastMeasurement.toModelItem(), isCompleted = false))
        }

        return brews
    }

    suspend fun save(scannedRaptPill: ScannedRaptPill) {
        val pill = scannedRaptPill.toDaoItem()
        val readings = scannedRaptPill.data.toDaoItem()
        dao.insertReadings(pill, readings)
    }

    suspend fun setIsOG(macAddress: String, timestamp: Instant, isOg: Boolean) {
        dao.setIsOG(
            macAddress = macAddress,
            timestamp = timestamp,
            isOG = isOg,
        )
    }

    suspend fun setIsFG(macAddress: String, timestamp: Instant, isOg: Boolean) {
        dao.setIsFG(
            macAddress = macAddress,
            timestamp = timestamp,
            isFg = isOg
        )
    }

    suspend fun updatePill(raptPill: RaptPill) {
        dao.updatePillData(raptPill = raptPill.toDaoItem())
    }

    fun observeData(macAddress: String): Flow<List<RaptPillData>> =
        dao.observeData(macAddress).map { data ->
            data.map { it.toModelItem() }
        }
}
