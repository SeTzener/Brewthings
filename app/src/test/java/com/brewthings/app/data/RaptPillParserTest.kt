package com.brewthings.app.data

import org.junit.Test

class RaptPillParserTest {
    @Test
    fun parseValidPillData() {
        val hexString = "0201061AFF524150540200000000000095AB44BA0232FC8BC52112796400"
        val byteArray = hexStringToByteArray(hexString)
        val parser = RaptPillParser()
        val data = parser.parse(byteArray)
        println(data)
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