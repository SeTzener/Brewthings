package com.brewthings.app.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

inline fun <T, C : List<T>> C.onEachReverse(
    action: (T) -> Unit,
): C {
    for (i in this.indices.reversed()) {
        action(this[i])
    }
    return this
}

fun Float.sumAll(others: Collection<Float>): Float = this + others.sum()

@Composable
fun <T : Any?> newOrCached(
    data: T,
    initialValue: T,
): T {
    var previousData: T by remember { mutableStateOf(initialValue) }
    return if (data != null) {
        previousData = data
        data
    } else {
        previousData
    }
}
