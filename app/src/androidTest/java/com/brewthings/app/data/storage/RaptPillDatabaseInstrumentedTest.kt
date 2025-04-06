package com.brewthings.app.data.storage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
}
