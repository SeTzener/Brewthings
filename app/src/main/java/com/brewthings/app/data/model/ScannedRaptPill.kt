package com.brewthings.app.data.model

data class ScannedRaptPill(
    val macAddress: String,
    val name: String?,
    val rssi: Int,
    val data: RaptPillData?
)
