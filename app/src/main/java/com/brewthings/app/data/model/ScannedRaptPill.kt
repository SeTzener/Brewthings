package com.brewthings.app.data.model

import com.brewthings.app.data.domain.Device

data class ScannedRaptPill(
    override val macAddress: String,
    override val name: String?,
    val rssi: Int,
    val data: ScannedRaptPillData,
) : Device
