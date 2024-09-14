package com.brewthings.app.util

import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

fun ByteArray.asHexString(prefix: String = "0x"): String = joinToString(
    prefix = prefix,
    separator = "",
    transform = {
        it.asHexString("")
    }
)

fun Byte.asHexString(prefix: String = "0x"): String =
    "$prefix${String.format(Locale.ENGLISH, "%02X", this)}"

fun floatingAngle(x: Float, y: Float, z: Float): Float = atan2(sqrt(x * x + y * y), z) * (180.0f / PI.toFloat())
