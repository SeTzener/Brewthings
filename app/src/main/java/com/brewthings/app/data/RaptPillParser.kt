package com.brewthings.app.data

import com.brewthings.app.data.model.RaptPillData
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RaptPillParser() {
    fun parse(data: ByteArray): RaptPillData {
        if (data.size!= 23) {
            throw IllegalArgumentException("Metrics data must have length 23")
        }

        // Convert byte array to ByteBuffer for easier manipulation
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)

        // Extract data from ByteBuffer
        val version = buffer.get()
        val mac = ByteArray(6).apply { buffer.get(this) }
        return RaptPillData(
                temperature = buffer.short.toUInt().toFloat(),
                gravity = buffer.float.toUInt().toFloat(),
                x = buffer.short.toUInt().toInt(),
                y = buffer.short.toUInt().toInt(),
                z = buffer.short.toUInt().toInt(),
                battery = buffer.short.toUInt().toFloat(),
        )
    }
}