package com.brewthings.app.data

import com.brewthings.app.data.model.RaptPillData
import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
Process advertisement with metrics.

This is what the advertisement data payload looks like in C,
endianness is big endian:

Version 1 (with MAC, no gravity velocity):

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
    char prefix[4];        // RAPT
    uint8_t version;       // always 0x02
    bool gravity_velocity_valid;
    float gravity_velocity;
    uint16_t temperature;  // x / 128 - 273.15
    float gravity;         // / 1000
    int16_t x;             // x / 16
    int16_t y;             // x / 16
    int16_t z;             // x / 16
    int16_t battery;       // x / 256
} RAPTPillMetricsV2;
*/

class RaptPillParser {
    fun parse(data: ByteArray): RaptPillData {
        // Find the start of the RAPT Pill data
        val raptStartIndex = data.indexOfFirst { it == 0x5241.toByte() } + 3 // "RA" in ASCII
        if (raptStartIndex == -1) {
            throw IllegalArgumentException("RAPT Pill data not found in the file")
        }

        // Ensure the RAPT Pill data has the correct length
        if (data.size - raptStartIndex < 20) {
            throw IllegalArgumentException("Metrics data must have length 23")
        }

        // Extract the RAPT Pill data
        val raptData = data.sliceArray(raptStartIndex until raptStartIndex + 21)

        // Convert byte array to ByteBuffer for easier manipulation
        val buffer = ByteBuffer.wrap(raptData).order(ByteOrder.BIG_ENDIAN)

        // Extract data from ByteBuffer
        val version = buffer.get()
        val gravityVelocityValid = buffer.get()!= 0.toByte()
        buffer.get() // for no reason at all
        val gravityVelocity = buffer.float
        val temperature = buffer.short.toUInt().toFloat()
        val gravity = buffer.float / 1000
        val x = buffer.short.toUInt().toInt() / 16
        val y = buffer.short.toUInt().toInt() / 16
        val z = buffer.short.toUInt().toInt() / 16
        val battery = buffer.short.toUInt().toFloat() / 256

        return RaptPillData(
            temperature = (temperature / 128.0 - 273.15).toFloat(),
            gravity = gravity,
            x = x,
            y = y,
            z = z,
            battery = battery,
        )
    }
}