package com.brewthings.app.data.model

data class RaptPill(
    val macAddress: String,
    val name: String?,
    val data: List<RaptPillData>
)
