package com.brewthings.app.data.repository

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.toDaoItem
import com.brewthings.app.data.storage.toModelItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class RaptPillRepository(
    private val scanner: RaptPillScanner,
    private val dao: RaptPillDao,
) {
    fun fromBluetooth(): Flow<ScannedRaptPill> = scanner.scan()

    fun fromDatabase(): Flow<List<RaptPill>> = dao.observeAll().map { query ->
        query.map { db ->
            RaptPill(
                macAddress = db.pill.macAddress,
                name = db.pill.name,
                data = db.data.map { data ->
                    data.toModelItem()
                },
            )
        }
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
            isFg = isOg,
        )
    }

    suspend fun updatePill(raptPill: RaptPill) {
        dao.updatePillData(raptPill = raptPill.toDaoItem())
    }

    fun observeData(macAddress: String): Flow<List<RaptPillData>> =
        dao.observeData(macAddress).map { data ->
            data.map { it.toModelItem() }
        }

    suspend fun setFeeding(macAddress: String, timestamp: Instant, isFeeding: Boolean) {
        dao.setFeeding(
            macAddress = macAddress,
            timestamp = timestamp,
            isFeeding = isFeeding,
        )
    }
}
