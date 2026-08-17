package com.brewthings.app.data.storage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

private const val TEST_DB_NAME = "rapt-db-test"

// These constants needs to be in sync with the data in sample.sql
private const val RAPT_PILL_COUNT = 1
private const val RAPT_PILL_DATA_COUNT = 343

@TestInstance(Lifecycle.PER_CLASS)
class RaptPillDatabaseInstrumentedTest {

    private lateinit var context: Context
    private lateinit var db: RaptPillDatabase

    @BeforeAll
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = RaptPillDatabase.create(context, TEST_DB_NAME)
    }

    @AfterAll
    fun tearDown() {
        db.close()
        context.deleteDatabase(TEST_DB_NAME)
    }

    @Test
    fun shouldInsertDataFromSampleSqlSuccessfully() {
        val dao = db.raptPillDao()

        val raptPillCount = dao.countRaptPills()
        assertEquals(RAPT_PILL_COUNT, raptPillCount)

        val raptPillDataCount = dao.countRaptPillData()
        assertEquals(RAPT_PILL_DATA_COUNT, raptPillDataCount)
    }

    @Test
    fun shouldOnlyReturnBrewEdgesForRequestedPill() = runBlocking {
        val isolatedDb = Room.inMemoryDatabaseBuilder(context, RaptPillDatabase::class.java).build()
        try {
            val dao = isolatedDb.raptPillDao()
            val requestedPill = RaptPill(macAddress = "AA:AA:AA:AA:AA:AA", name = "Requested")
            val otherPill = RaptPill(macAddress = "BB:BB:BB:BB:BB:BB", name = "Other")

            dao.insertReadings(requestedPill, readingsAt("2026-08-01T00:00:00Z", isOG = true))
            dao.insertReadings(requestedPill, readingsAt("2026-08-02T00:00:00Z", isFG = true))
            dao.insertReadings(otherPill, readingsAt("2026-08-03T00:00:00Z", isOG = true))
            dao.insertReadings(otherPill, readingsAt("2026-08-04T00:00:00Z", isFG = true))

            val requestedPillId = dao.getPillIdByMacAddress(requestedPill.macAddress)
            val edges = dao.getBrewEdges(requestedPill.macAddress)

            assertEquals(2, edges.size)
            assertEquals(setOf(requestedPillId), edges.map { it.pillId }.toSet())
        } finally {
            isolatedDb.close()
        }
    }

    private fun readingsAt(
        timestamp: String,
        isOG: Boolean? = null,
        isFG: Boolean? = null,
    ) = RaptPillReadings(
        timestamp = Instant.parse(timestamp),
        temperature = 20f,
        gravity = 1.050f,
        gravityVelocity = null,
        x = 0f,
        y = 0f,
        z = 0f,
        battery = 1f,
        isOG = isOG,
        isFG = isFG,
        isFeeding = null,
    )
}
