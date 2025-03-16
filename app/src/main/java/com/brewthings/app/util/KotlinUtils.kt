package com.brewthings.app.util

import java.util.Locale

fun ByteArray.asHexString(prefix: String = "0x"): String = joinToString(
    prefix = prefix,
    separator = "",
    transform = {
        it.asHexString("")
    },
)

fun Byte.asHexString(prefix: String = "0x"): String =
    "$prefix${String.format(Locale.ENGLISH, "%02X", this)}"

fun Float.sumAll(others: Collection<Float>): Float = this + others.sum()

fun Float.toPercent(): Float = this * 0.01f
