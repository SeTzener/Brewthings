package com.brewthings.app.data.storage

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    indices =
    [
        Index(value = ["id"], unique = true),
        Index(value = ["start"], unique = true),
    ],
)
data class BrewData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val notes: String,
    val start: Instant,
)
