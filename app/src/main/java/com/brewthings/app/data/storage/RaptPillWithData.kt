package com.brewthings.app.data.storage

import androidx.room.Embedded
import androidx.room.Relation

data class RaptPillWithData(
    @Embedded val pill: RaptPill,
    @Relation(
        parentColumn = "pillId",
        entityColumn = "pillId"
    )
    val data: List<RaptPillData>
)
