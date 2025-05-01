package com.brewthings.app.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brewthings.app.R
import com.brewthings.app.data.storage.migrations.MIGRATION_1_2
import com.brewthings.app.data.storage.migrations.MIGRATION_2_3
import com.brewthings.app.data.storage.migrations.MIGRATION_3_4
import com.brewthings.app.data.storage.migrations.MIGRATION_4_5
import com.brewthings.app.data.utils.ReadScript
import com.brewthings.app.util.Logger
import com.google.firebase.components.BuildConfig

@Database(entities = [RaptPill::class, RaptPillData::class, BrewData::class], version = 5)
@androidx.room.TypeConverters(TypeConverter::class)
abstract class RaptPillDatabase : RoomDatabase() {
    abstract fun raptPillDao(): RaptPillDao

    companion object {
        private const val DATABASE_NAME = "rapt-db"

        private val logger = Logger("RaptPillDatabase")

        fun create(context: Context, dbName: String = DATABASE_NAME): RaptPillDatabase {
            return Room.databaseBuilder(
                context,
                RaptPillDatabase::class.java,
                dbName,
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    if (BuildConfig.DEBUG) {
                        logger.info("Inserting initial data.")
                        try {
                            ReadScript.insertFromFile(context, R.raw.sample, db)
                        } catch (e: Exception) {
                            logger.error("Failed to insert initial data.", e)
                        }
                    }
                }
            }).build()
        }
    }
}
