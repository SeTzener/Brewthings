package com.brewthings.app.data.repository

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillReadings
import com.brewthings.app.data.storage.RaptPillWithData
import kotlinx.coroutines.flow.Flow

class RaptPillRepository(
    private val scanner: RaptPillScanner,
    private val dao: RaptPillDao
) {
    fun fromBluetooth(): Flow<RaptPill> = scanner.scan()

    fun fromDatabase(): Flow<List<RaptPillWithData>> = dao.observeAll()

    fun save(raptPill: RaptPill) {
        val pill = com.brewthings.app.data.storage.RaptPill(
            macAddress = raptPill.macAddress,
            name = raptPill.name,
        )
        val readings = raptPill.data?.let {
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
