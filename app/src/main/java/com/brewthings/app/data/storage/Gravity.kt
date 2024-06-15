package com.brewthings.app.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["dataId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = RaptPillData::class,
            parentColumns = arrayOf("dataId"),
            childColumns = arrayOf("dataId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RaptPill::class,
            parentColumns = arrayOf("pillId"),
            childColumns = arrayOf("pillId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Gravity(
    @PrimaryKey(autoGenerate = true) val gravityId: Long = 0,
    val pillId: Long,
    val dataId: Long,
    @ColumnInfo val isOG: Boolean?,
    @ColumnInfo val isFG: Boolean?,
)