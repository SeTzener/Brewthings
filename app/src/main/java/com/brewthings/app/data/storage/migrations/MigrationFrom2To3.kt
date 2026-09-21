package com.brewthings.app.data.storage.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Adding new columns to the RaptPillReadings table
        db.execSQL("ALTER TABLE RaptPillData ADD COLUMN gravityVelocity REAL")
    }
}
