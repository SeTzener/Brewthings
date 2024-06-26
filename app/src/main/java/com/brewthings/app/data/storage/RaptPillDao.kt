package com.brewthings.app.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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
                "ORDER BY RaptPillData.timestamp ASC LIMIT 1"
    )
    fun observeOG(macAddress: String): Flow<RaptPillData?> // TODO: change to query set OG.

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
