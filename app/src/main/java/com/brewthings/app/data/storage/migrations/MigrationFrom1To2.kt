package com.brewthings.app.data.storage.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Adding new columns to the RaptPillReadings table
        db.execSQL("ALTER TABLE RaptPillData ADD COLUMN isOG INTEGER")
        db.execSQL("ALTER TABLE RaptPillData ADD COLUMN isFG INTEGER")
    }
}
