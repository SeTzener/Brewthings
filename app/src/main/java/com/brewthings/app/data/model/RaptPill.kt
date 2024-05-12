package com.brewthings.app.data.model

data class RaptPill(
    val macAddress: String,
    val name: String?,
    val rssi: Int,
    val data: RaptPillData?
)
