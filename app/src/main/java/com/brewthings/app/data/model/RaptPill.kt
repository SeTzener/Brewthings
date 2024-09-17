package com.brewthings.app.data.model

import com.brewthings.app.data.domain.Device

data class RaptPill(
    override val macAddress: String,
    override val name: String?,
    val data: List<RaptPillData>
) : Device
