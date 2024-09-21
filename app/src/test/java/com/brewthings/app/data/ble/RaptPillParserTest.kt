package com.brewthings.app.data.ble

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RaptPillParserTest {
    @Test
    fun parseValidPillData() {
        val hexString = "5054020001C01D9DBD95AB44BA0232FC8BC52112796400"
        val byteArray = hexStringToByteArray(hexString)
        val data = RaptPillParser.parse(byteArray)
        with (data){
            assertTrue(gravityVelocity == -2.4627526f)
            assertTrue(temperature == 26.185938f)
            assertTrue(gravity == 1.4880686f)
            assertTrue(x == 4040.6875f)
            assertTrue(y == 3154.0625f)
            assertTrue(z == 295.5625f)
            assertTrue(battery.toDouble() == 100.0)

        }
    }
    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }
}
