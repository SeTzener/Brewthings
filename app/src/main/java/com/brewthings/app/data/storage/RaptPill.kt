package com.brewthings.app.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["mac_address"], unique = true)]
)
data class RaptPill(
    @PrimaryKey(autoGenerate = true) val pillId: Long = 0,
    @ColumnInfo val macAddress: String,
    @ColumnInfo val name: String?,
)
