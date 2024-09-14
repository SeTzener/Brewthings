package com.brewthings.app.data.repository

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillReadings
import com.brewthings.app.data.storage.toDataItem
import com.brewthings.app.data.storage.toModelItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    suspend fun save(scannedRaptPill: ScannedRaptPill) {
        val pill = com.brewthings.app.data.storage.RaptPill(
            macAddress = scannedRaptPill.macAddress,
            name = scannedRaptPill.name,
        )
        val readings = scannedRaptPill.data?.let {
            RaptPillReadings(
                timestamp = it.timestamp,
                temperature = it.temperature,
                gravity = it.gravity,
                gravityVelocity = it.gravityVelocity,
                x = it.x,
                y = it.y,
                z = it.z,
                battery = it.battery,
                isOG = null,
                isFG = null
            )
        }
        dao.insertReadings(pill, readings)
    }

    suspend fun setIsOG(macAddress: String, timestamp: Instant, isOg: Boolean){
        dao.setIsOG(
            macAddress = macAddress,
            timestamp = timestamp,
            isOG = isOg,
        )
    }

    suspend fun setIsFG(macAddress: String, timestamp: Instant, isOg: Boolean){
        dao.setIsFG(
            macAddress = macAddress,
            timestamp = timestamp,
            isFg = isOg
        )
    }

    suspend fun updatePill(raptPill: RaptPill) {
        dao.updatePillData(raptPill = raptPill.toDataItem())
    }

    fun observeData(macAddress: String): Flow<List<RaptPillData>> =
        dao.observeData(macAddress).map { data ->
            data.map { it.toModelItem() }
        }
}
