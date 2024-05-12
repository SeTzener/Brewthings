package com.brewthings.app.data.ble

import junit.framework.TestCase.assertTrue
import org.junit.Test

class RaptPillParserTest {
    @Test
    fun parseValidPillData() {
        val hexString = "50540200000000000095AB44BA0232FC8BC52112796400"
        val byteArray = hexStringToByteArray(hexString)
        val data = RaptPillParser.parse(byteArray)
        with (data){
            assertTrue(temperature.toDouble() == 26.185937881469727)
            assertTrue(gravity.toDouble() == 1.4880685806274414)
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
