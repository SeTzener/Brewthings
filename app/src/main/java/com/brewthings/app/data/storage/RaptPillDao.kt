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
    @Query("SELECT * FROM RaptPill WHERE macAddress = :macAddress")
    fun observePillWithData(macAddress: String): Flow<RaptPillWithData?>

    @Query(
        "SELECT * FROM RaptPillData " +
                "JOIN RaptPill ON RaptPill.pillId = RaptPillData.pillId " +
                "WHERE RaptPill.macAddress = :macAddress"
    )
    fun observeData(macAddress: String): Flow<List<RaptPillData>>

    @Query("SELECT pillId FROM RaptPill WHERE macAddress = :macAddress")
    fun getPillIdByMacAddress(macAddress: String): Long?

    @Query("SELECT name FROM RaptPill WHERE macAddress = :macAddress")
    fun getPillNameByMacAddress(macAddress: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(raptPill: RaptPill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(raptPillData: RaptPillData)

    @Transaction
    fun insertName(macAddress: String, name: String?) {
        val pillId = getPillIdByMacAddress(macAddress)
            ?: throw IllegalArgumentException("No pill found with mac address $macAddress")
        insert(RaptPill(pillId = pillId, macAddress = macAddress, name = name))
    }

    @Transaction
    fun insertReadings(macAddress: String, raptPillReadings: RaptPillReadings) {
        val pillId = getPillIdByMacAddress(macAddress)
            ?: throw IllegalArgumentException("No pill found with mac address $macAddress")
        val data = RaptPillData(pillId = pillId, readings = raptPillReadings)
        insertData(data)
    }
}
