package com.brewthings.app.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("SELECT pillId FROM RaptPill WHERE macAddress = :macAddress")
    suspend fun getPillIdByMacAddress(macAddress: String): Long?

    @Query("SELECT name FROM RaptPill WHERE macAddress = :macAddress")
    suspend fun getPillNameByMacAddress(macAddress: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPill(raptPill: RaptPill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(raptPillData: RaptPillData)

    @Transaction
    suspend fun insertName(macAddress: String, name: String?) {
        val pillId = getPillIdByMacAddress(macAddress)
            ?: throw IllegalArgumentException("No pill found with mac address $macAddress")
        insertPill(RaptPill(pillId = pillId, macAddress = macAddress, name = name))
    }

    @Transaction
    suspend fun insertReadings(raptPill: RaptPill, raptPillReadings: RaptPillReadings?) {
        insertPill(raptPill)
        raptPillReadings?.also { insertData(RaptPillData(pillId = raptPill.pillId, readings = it)) }
    }
}
