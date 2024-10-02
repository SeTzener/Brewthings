package com.brewthings.app.data.storage

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.brewthings.app.data.domain.SensorWithTiltReadings
import kotlinx.datetime.Instant

@Entity(
    indices = [Index(value = ["dataId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = RaptPillData::class,
            parentColumns = arrayOf("dataId"),
            childColumns = arrayOf("dataId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RaptPillReadings(
    override val timestamp: Instant,
    override val temperature: Float,
    override val gravity: Float,
    override val gravityVelocity: Float?,
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val battery: Float,
    val isOG: Boolean?,
    val isFG: Boolean?,
) : SensorWithTiltReadings
