package com.brewthings.app.util

import kotlin.math.roundToLong
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * Returns the default [TimeZone] of the system.
 */
fun systemTimeZone(): TimeZone = TimeZone.currentSystemDefault()

/**
 * Merges two ranges into one. The resulting range is from the minimal start value of both ranges to the maximal end
 * values of both ranges.
 */
fun <T : Comparable<T>> ClosedRange<T>.mergeWith(range: ClosedRange<T>): ClosedRange<T> =
    minOf(start, range.start)..maxOf(endInclusive, range.endInclusive)

fun ClosedRange<Instant>.contains(otherRange: ClosedRange<Instant>): Boolean =
    contains(otherRange.start) && contains(otherRange.endInclusive)

/**
 * Get the current instant.
 */
fun now(): Instant = Clock.System.now()

/**
 * Get the current [Instant] rounded to the current hour.
 */
fun currentHour(): Instant =
    Instant.fromEpochSeconds(now().epochSeconds.floorSecToHour())

fun startOfDay(now: Instant = now(), timeZone: TimeZone = systemTimeZone()): Instant {
    return now
        .toLocalDateTime(timeZone)
        .date
        .atStartOfDayIn(timeZone)
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

/**
 * Rounds down the given seconds since epoch [Long] to the closest hour.
 */
fun Long.floorSecToHour(): Long =
    HOUR_IN_SEC * secondsToHours().toLong()

/**
 * Adds one hour to [Instant].
 */
fun Instant.plusHour(): Instant =
    plus(1, DateTimeUnit.HOUR)

private fun Long.msToHours(): Double =
    this / HOUR_IN_MS.toDouble()

private fun Long.secondsToHours(): Double =
    this / HOUR_IN_SEC.toDouble()

const val DAY_IN_SEC = 86400
internal const val SEC_IN_MS: Long = 1000L
private const val HOUR_IN_SEC = 60 * 60
private const val HOUR_IN_MS = HOUR_IN_SEC * SEC_IN_MS
