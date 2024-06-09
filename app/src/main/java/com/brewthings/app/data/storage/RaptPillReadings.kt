package com.brewthings.app.data.storage

import kotlinx.datetime.Instant

data class RaptPillReadings(
    val timestamp: Instant,
    val temperature: Float,
    val gravity: Float,
    val x: Float,
    val y: Float,
    val z: Float,
    val battery: Float
)
