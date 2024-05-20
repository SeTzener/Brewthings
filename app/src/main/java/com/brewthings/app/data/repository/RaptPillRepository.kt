package com.brewthings.app.data.repository

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillReadings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
                    RaptPillData(
                        timestamp = data.readings.timestamp,
                        temperature = data.readings.temperature,
                        gravity = data.readings.gravity,
                        x = data.readings.x,
                        y = data.readings.y,
                        z = data.readings.z,
                        battery = data.readings.battery,
                    )
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
                x = it.x,
                y = it.y,
                z = it.z,
                battery = it.battery,
            )
        }
        dao.insertReadings(pill, readings)
    }
}
