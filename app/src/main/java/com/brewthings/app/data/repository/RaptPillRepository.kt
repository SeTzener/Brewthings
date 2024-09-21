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
        val edges = dao.getBrewEdges(macAddress)

        if (edges.first().firstOrNull() == null ){
            return emptyList()
        }
        val lastMeasurement: RaptPillReadings = edges.map {
            it.takeIf { query ->
                query.last().readings.isFG == true
            }?.first()?.readings
        }.firstOrNull() ?: dao.getLastMeasurement(macAddress).first().readings

        edges.collect { dataList ->
            var currentOg: RaptPillData? = null
            var secondOg: RaptPillData? = null

            dataList.forEach { measurement ->
                if (measurement.readings.isOG == true) {
                    if (currentOg == null) {
                        // First OG found
                        currentOg = measurement.readings.toModelItem()
                    } else if (secondOg == null) {
                        // Second OG found, treat it as FG
                        secondOg = measurement.readings.toModelItem()
                        // Add brew with second OG as fgOrLast
                        brews.add(Brew(og = currentOg!!, fgOrLast = secondOg!!, isCompleted = true))
                        // Reset current OG for the next brew
                        currentOg = null
                        secondOg = null
                    }
                } else if (currentOg != null) {
                    // FG found after OG
                    brews.add(
                        Brew(
                            og = currentOg!!,
                            fgOrLast = measurement.readings.toModelItem(),
                            isCompleted = true
                        )
                    )
                    // Reset current OG for the next brew
                    currentOg = null
                }
            }

            // If there's an uncompleted OG, pair it with the last measurement
            if (currentOg != null && lastMeasurement != null) {
                brews.add(Brew(og = currentOg!!, fgOrLast = lastMeasurement.toModelItem(), isCompleted = false))
            }
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
