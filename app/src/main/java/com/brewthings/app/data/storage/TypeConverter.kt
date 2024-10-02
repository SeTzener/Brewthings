package com.brewthings.app.data.storage

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class TypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}
