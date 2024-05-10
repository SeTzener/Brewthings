package com.brewthings.app.ble

import com.brewthings.app.util.asHexString

data class RaptPill(
    val macAddress: String,
    val name: String,
    val manufacturerData: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RaptPill) return false

        if (macAddress != other.macAddress) return false
        if (name != other.name) return false
        if (manufacturerData != null) {
            if (other.manufacturerData == null) return false
            if (!manufacturerData.contentEquals(other.manufacturerData)) return false
        } else if (other.manufacturerData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = macAddress.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (manufacturerData?.contentHashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "BleDevice(macAddress='$macAddress', name='$name', manufacturerData=${manufacturerData?.asHexString()})"
    }
}
