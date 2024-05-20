package com.brewthings.app.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RaptPill::class, RaptPillData::class], version = 1)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class RaptPillDatabase : RoomDatabase() {
    abstract fun raptPillDao(): RaptPillDao
}
