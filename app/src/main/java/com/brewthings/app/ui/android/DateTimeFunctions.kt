package com.brewthings.app.ui.android

import java.time.Duration
import java.time.Instant
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

fun Instant.isWithin(instant: Instant, duration: Duration): Boolean {
    val diff = (instant.epochSecond - epochSecond).absoluteValue
    return diff <= duration.seconds
}

/**
 * Rounds the given milliseconds since epoch [Long] to the closest hour.
 */
fun Long.roundMsToHour(): Long =
    HOUR_IN_MS * msToHours().roundToLong()

/**
 * Rounds the given seconds since epoch [Long] to the closest hour.
 */
fun Long.roundSecToHour(): Long =
    HOUR_IN_SEC * secondsToHours().roundToLong()

private fun Long.msToHours(): Double =
    this / HOUR_IN_MS.toDouble()

private fun Long.secondsToHours(): Double =
    this / HOUR_IN_SEC.toDouble()

internal const val SEC_IN_MS: Long = 1000L
private const val HOUR_IN_SEC = 60 * 60
private const val HOUR_IN_MS = HOUR_IN_SEC * SEC_IN_MS
