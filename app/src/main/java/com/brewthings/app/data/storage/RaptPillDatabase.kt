package com.brewthings.app.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brewthings.app.R
import com.brewthings.app.data.utils.ReadScript
import com.brewthings.app.util.Logger

@Database(entities = [RaptPill::class, RaptPillData::class], version = 2)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class RaptPillDatabase : RoomDatabase() {
    abstract fun raptPillDao(): RaptPillDao

    companion object {
        private const val DATABASE_NAME = "rapt-db"

        private val logger = Logger("RaptPillDatabase")
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Adding new columns to the RaptPillReadings table
                database.execSQL("ALTER TABLE RaptPillData ADD COLUMN isOG INTEGER")
                database.execSQL("ALTER TABLE RaptPillData ADD COLUMN isFG INTEGER")
            }
        }


        fun create(context: Context): RaptPillDatabase {
            return Room.databaseBuilder(
                context,
                RaptPillDatabase::class.java,
                DATABASE_NAME
            ).addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    try {
                        // Inserting initial data
                        ReadScript.insertFromFile(context, R.raw.sample, db)
                    } catch (e: Exception) {
                        logger.error("Failed to insert initial data.", e)
                    }
                }
            }).build()
        }
    }
}
