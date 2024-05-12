package com.brewthings.app.data.utils

import java.nio.ByteBuffer

fun ByteBuffer.toUShort(): UShort {
    require(limit() >= position() + 2) { "Buffer must have at least 2 bytes for uShort conversion" }

    val firstByte = get().toUInt() and 0xFFu shl 8
    val secondByte = get().toUInt() and 0xFFu

    return (firstByte or secondByte).toUShort()
}