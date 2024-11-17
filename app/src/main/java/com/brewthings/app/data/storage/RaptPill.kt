package com.brewthings.app.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.brewthings.app.data.domain.Device

@Entity(
    indices = [Index(value = ["macAddress"], unique = true)],
)
data class RaptPill(
    @PrimaryKey(autoGenerate = true) val pillId: Long = 0,
    @ColumnInfo override val macAddress: String,
    @ColumnInfo override val name: String?,
) : Device
