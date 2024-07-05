package com.brewthings.app.data.storage

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.datetime.Instant

@Entity(
    indices = [Index(value = ["dataId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = RaptPillData::class,
            parentColumns = arrayOf("dataId"),
            childColumns = arrayOf("dataId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RaptPillReadings(
    val timestamp: Instant,
    val temperature: Float,
    val gravity: Float,
    val x: Float,
    val y: Float,
    val z: Float,
    val battery: Float,
    val isOG: Boolean?,
    val isFG: Boolean?
)
