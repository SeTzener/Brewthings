package com.brewthings.app.data.storage.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
               CREATE TABLE IF NOT EXISTS BrewData (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    notes TEXT NOT NULL,
                    start INTEGER NOT NULL,
                    UNIQUE(id)
               )
                """,
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_BrewData_id ON BrewData(id)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_BrewData_start ON BrewData(start)")
    }
}
