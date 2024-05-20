package com.brewthings.app.data.storage

import androidx.room.TypeConverter
import java.time.Instant

class TypeConverters {
  @TypeConverter
  fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

  @TypeConverter
  fun dateToTimestamp(instant: Instant?): Long? = instant?.toEpochMilli()
}
