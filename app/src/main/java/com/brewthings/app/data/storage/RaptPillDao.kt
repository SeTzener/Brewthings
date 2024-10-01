package com.brewthings.app.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface RaptPillDao {
    @Transaction
    @Query("SELECT * FROM RaptPill")
    fun observeAll(): Flow<List<RaptPillWithData>>

    @Query(
        "SELECT * FROM RaptPillData " +
                "JOIN RaptPill ON RaptPill.pillId = RaptPillData.pillId " +
                "WHERE RaptPill.macAddress = :macAddress"
    )
    fun observeData(macAddress: String): Flow<List<RaptPillData>>

    @Query(
        "SELECT * FROM RaptPillData " +
                "JOIN RaptPill ON RaptPill.pillId = RaptPillData.pillId " +
                "WHERE RaptPill.macAddress = :macAddress " +
                "AND RaptPillData.timestamp <= :timestamp " +
                "ORDER BY RaptPillData.timestamp DESC LIMIT 2"
    )
    fun observeDataAndPrevious(macAddress: String, timestamp: Instant): Flow<List<RaptPillData>>

    @Query(
        "SELECT * FROM RaptPillData " +
                "JOIN RaptPill ON RaptPill.pillId = RaptPillData.pillId " +
                "WHERE RaptPill.macAddress = :macAddress " +
                "AND RaptPillData.isOG == 1 " +
                "OR RaptPillData.isFG == 1 " +
                "ORDER BY RaptPillData.timestamp ASC"
    )
    fun getBrewEdges(macAddress: String): Flow<List<RaptPillData>>

    @Query(
        "SELECT * FROM RaptPillData " +
                "JOIN RaptPill ON RaptPill.pillId = RaptPillData.pillId " +
                "WHERE RaptPill.macAddress = :macAddress ORDER BY RaptPillData.dataId DESC " +
                "LIMIT 1"
    )
    fun getLastMeasurement(macAddress: String): Flow<RaptPillData>

    @Query(
        "SELECT * FROM RaptPillData " +
                "JOIN RaptPill ON RaptPill.pillId = RaptPillData.pillId " +
                "WHERE RaptPill.macAddress = :macAddress ORDER BY RaptPillData.dataId ASC " +
                "LIMIT 1"
    )
    fun getFirstMeasurement(macAddress: String): Flow<RaptPillData>

    @Query(
        "SELECT * FROM RaptPillData " +
                "JOIN RaptPill ON RaptPill.pillId = RaptPillData.pillId " +
                "WHERE RaptPill.macAddress = :macAddress " +
                "AND RaptPillData.timestamp == :timestamp "
    )
    suspend fun getPillData(macAddress: String, timestamp: Instant): RaptPillData

    @Transaction
    suspend fun setIsOG(macAddress: String, timestamp: Instant, isOG: Boolean) {
        val data = getPillData(macAddress, timestamp)
        insertData(
            data.copy(
                readings = data.readings.copy(isOG = isOG)
            )
        )
    }

    @Transaction
    suspend fun setIsFG(macAddress: String, timestamp: Instant, isFg: Boolean) {
        val data: RaptPillData = getPillData(macAddress, timestamp)
        insertData(
            data.copy(
                readings = data.readings.copy(isFG = isFg)
            )
        )
    }

    @Query("SELECT pillId FROM RaptPill WHERE macAddress = :macAddress")
    suspend fun getPillIdByMacAddress(macAddress: String): Long?

    @Query("SELECT name FROM RaptPill WHERE macAddress = :macAddress")
    suspend fun getPillNameByMacAddress(macAddress: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPill(raptPill: RaptPill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(raptPillData: RaptPillData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePill(raptPill: RaptPill)

    @Transaction
    suspend fun updatePillData(raptPill: RaptPill) {
        val pillId = getPillIdByMacAddress(raptPill.macAddress)
            ?: error("No pill found with mac address ${raptPill.macAddress}")
        updatePill(raptPill.copy(pillId = pillId))
    }

    @Transaction
    suspend fun insertReadings(raptPill: RaptPill, raptPillReadings: RaptPillReadings?) {
        val pillId = getPillIdByMacAddress(raptPill.macAddress)
            ?: run {
                insertPill(raptPill)
                getPillIdByMacAddress(raptPill.macAddress)
            } ?: error("No pill found with mac address ${raptPill.macAddress}")
        raptPillReadings?.also { insertData(RaptPillData(pillId = pillId, readings = it)) }
    }
}
