package com.brewthings.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.model.MacAddress
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillWithData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.toDaoItem
import com.brewthings.app.data.storage.toModelItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

private val SELECTED_PILL = stringPreferencesKey("selected_pill")

class RaptPillRepository(
    private val scanner: RaptPillScanner,
    private val dao: RaptPillDao,
    private val dataStore: DataStore<Preferences>,
) {
    fun fromBluetooth(): Flow<ScannedRaptPill> = scanner.scan()

    fun fromDatabase(): Flow<List<RaptPillWithData>> = dao.observeAll().map { query ->
        query.map { db ->
            RaptPillWithData(
                raptPill = RaptPill(
                    macAddress = db.pill.macAddress,
                    name = db.pill.name,
                ),
                data = db.data.map { data ->
                    data.toModelItem()
                },
            )
        }
    }

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

    suspend fun updatePill(raptPill: RaptPillWithData) {
        dao.updatePillData(raptPill = raptPill.raptPill.toDaoItem())
    }

    fun observeData(macAddress: MacAddress): Flow<List<RaptPillData>> =
        dao.observeData(macAddress).map { data ->
            data.map { it.toModelItem() }
        }

    fun observeLatestData(macAddress: MacAddress): Flow<RaptPillData?> =
        dao.observeLatestData(macAddress).map { data ->
            data?.toModelItem()
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

    fun observeSelectedPill(): Flow<MacAddress?> = dataStore.data.map { preferences ->
        val saved = preferences[SELECTED_PILL]
        saved ?: dao.getFirstPillMacAddress()
    }

    suspend fun selectPill(macAddress: MacAddress) = dataStore.edit { preferences ->
        preferences[SELECTED_PILL] = macAddress
    }
}
