package com.brewthings.app.data.repository

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.model.MacAddress
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

    suspend fun save(
        scannedRaptPill: ScannedRaptPill,
        isOg: Boolean? = null,
        isFg: Boolean? = null,
        isFeeding: Boolean? = null,
    ) {
        val pill = scannedRaptPill.toDaoItem()
        val readings = scannedRaptPill.data.toDaoItem(isOg = isOg, isFg = isFg, isFeeding = isFeeding)
        dao.insertReadings(pill, readings)
    }

    suspend fun setIsOG(macAddress: MacAddress, timestamp: Instant, isOg: Boolean) {
        dao.setIsOG(
            macAddress = macAddress,
            timestamp = timestamp,
            isOG = isOg,
        )
    }

    suspend fun setIsFG(macAddress: MacAddress, timestamp: Instant, isOg: Boolean) {
        dao.setIsFG(
            macAddress = macAddress,
            timestamp = timestamp,
            isFg = isOg,
        )
    }

    suspend fun updatePillName(macAddress: MacAddress, newName: String) {
        dao.updatePillName(macAddress, newName)
    }

    fun observeData(macAddress: MacAddress): Flow<List<RaptPillData>> =
        dao.observeData(macAddress).map { data ->
            data.map { it.toModelItem() }
        }

    suspend fun setFeeding(macAddress: MacAddress, timestamp: Instant, isFeeding: Boolean) {
        dao.setFeeding(
            macAddress = macAddress,
            timestamp = timestamp,
            isFeeding = isFeeding,
        )
    }

    fun observePills(): Flow<List<RaptPill>> = dao.observePills().map { pills ->
        pills.map { pill ->
            RaptPill(
                macAddress = pill.macAddress,
                name = pill.name,
            )
        }
    }

    suspend fun deleteMeasurement(macAddress: MacAddress, timestamp: Instant) {
        val pillId = dao.getPillIdByMacAddress(macAddress) ?: error("No pill found with mac address $macAddress")
        val dataId = dao.getPillData(macAddress, timestamp).dataId
        dao.deletePillData(pillId = pillId, dataId = dataId)
    }
}
