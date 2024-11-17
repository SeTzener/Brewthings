package com.brewthings.app.data.storage

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["pillId", "timestamp"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = RaptPill::class,
            parentColumns = arrayOf("pillId"),
            childColumns = arrayOf("pillId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RaptPillData(
    @PrimaryKey(autoGenerate = true) val dataId: Long = 0,
    val pillId: Long,
    @Embedded val readings: RaptPillReadings,
)
