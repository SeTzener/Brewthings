package com.brewthings.app.data.ble

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RaptPillWithDataParserTest {
    @Test
    fun parseValidPillData() {
        val byteArray = "5054020001C01D9DBD95AB44BA0232FC8BC52112796400".toByteArray()
        val data = RaptPillParser.parse(byteArray)
        with(data) {
            assertTrue(gravityVelocity == -2.4627526f)
            assertTrue(temperature == 26.185938f)
            assertTrue(gravity == 1.4880686f)
            assertTrue(x == 4040.6875f)
            assertTrue(y == 3154.0625f)
            assertTrue(z == 295.5625f)
            assertTrue(battery.toDouble() == 100.0)
        }
    }
    private fun String.toByteArray(): ByteArray {
        val data = ByteArray(length / 2)
        for (i in indices step 2) {
            data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        }
        return data
    }
}
