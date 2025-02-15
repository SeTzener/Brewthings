package com.brewthings.app.data.domain

interface Device {
    val macAddress: String
    val name: String?

    val displayName get() = name ?: macAddress
}

data class MockDevice(
    override val macAddress: String,
    override val name: String?,
) : Device
