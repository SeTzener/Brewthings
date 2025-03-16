package com.brewthings.app.data.model

import com.brewthings.app.data.domain.Device

data class RaptPill(
    override val macAddress: MacAddress,
    override val name: String?,
) : Device
