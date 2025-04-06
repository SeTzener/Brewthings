package com.brewthings.app.data.ble

import com.brewthings.app.data.model.ScannedRaptPillData
import com.brewthings.app.data.utils.toUShort
import kotlinx.datetime.Clock
import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
Process advertisement with metrics.

This is what the advertisement data payload looks like in C,
endianness is big endian:

Version 1 not supported (with MAC, no gravity velocity):

typedef struct __attribute__((packed)) {
    char prefix[4];        // RAPT
    uint8_t version;       // always 0x01
    uint8_t mac[6];
    uint16_t temperature;  // x / 128 - 273.15
    float gravity;         // / 1000
    int16_t x;             // x / 16
    int16_t y;             // x / 16
    int16_t z;             // x / 16
    int16_t battery;       // x / 256
} RAPTPillMetricsV1;

Version 2 (no MAC, with gravity velocity):

typedef struct __attribute__((packed)) {
    char prefix[4];              // RAPT
    uint8_t version;             // always 0x02
    0x00                         // not sure what this is
    bool gravity_velocity_valid; // 0x00 or 0x01
    float gravity_velocity;      // not sure what the unit is
    uint16_t temperature;        // x / 128 - 273.15
    float gravity;               // / 1000
    int16_t x;                   // x / 16
    int16_t y;                   // x / 16
    int16_t z;                   // x / 16
    int16_t battery;             // x / 256
} RAPTPillMetricsV2;
*/

object RaptPillParser {
    fun parse(data: ByteArray): ScannedRaptPillData {
        if (data.size != 23) {
            throw IllegalArgumentException("Metrics data must have length 23")
        }

        if (data[0] != 'P'.code.toByte() || data[1] != 'T'.code.toByte()) {
            throw IllegalArgumentException("Metrics data must start with `P` `T`")
        }

        val expectedVersion = 2u
        if (data[2] != expectedVersion.toByte()) {
            throw IllegalArgumentException("Version not supported: expected $expectedVersion, got ${data[2].toUInt()}")
        }

        // Convert byte array to ByteBuffer for easier manipulation
        val buffer = ByteBuffer.wrap(data.copyOfRange(4, data.size)).order(ByteOrder.BIG_ENDIAN)

        // Extract data from ByteBuffer
        val gravityVelocityValid = buffer.get() != 0.toByte()
        val gravityVelocity = buffer.float

        val temperature = buffer.toUShort().toFloat()
        val gravity = buffer.float / 1000
        val x = buffer.toUShort().toInt() / 16f
        val y = buffer.toUShort().toInt() / 16f
        val z = buffer.toUShort().toInt() / 16f
        val battery = buffer.toUShort().toFloat() / 256f

        return ScannedRaptPillData(
            timestamp = Clock.System.now(),
            temperature = (temperature / 128.0 - 273.15).toFloat(),
            gravity = gravity,
            rawVelocity = if (gravityVelocityValid) gravityVelocity.sanitizeVelocity() else null,
            x = x,
            y = y,
            z = z,
            battery = battery,
        )
    }
}

private fun Float.sanitizeVelocity(): Float? =
    if (isInfinite() || isNaN() || this < -100 || this > 100) {
        null // Invalid velocity.
    } else {
        -1 * this // Invert the sign, to make it more intuitive.
    }
