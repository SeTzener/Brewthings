package com.brewthings.app.ui.screens.scanning

import com.brewthings.app.data.model.Brew
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.juul.kable.Bluetooth
import kotlinx.datetime.Instant

const val RSSI_THRESHOLD_RANGE_START = 0f
const val RSSI_THRESHOLD_RANGE_END = -200f
const val INITIAL_RSSI_THRESHOLD = -100

data class ScanningScreenState(
    val bluetooth: Bluetooth.Availability? = null,
    val rssiThreshold: Int = INITIAL_RSSI_THRESHOLD,
    val scanning: Boolean = false,
    val scannedPillsCount: Int = 0,
    val scannedPills: List<ScannedRaptPill> = emptyList(),
    val savedPills: List<RaptPill> = emptyList(),
    val brews: List<Brew> = listOf(testBrew, testBrew2)
)

// TODO(Tano): Remove this once the brews logic is finished
val testBrew  = Brew(
    og = RaptPillData(
        timestamp = Instant.fromEpochMilliseconds(1716738391308),
        temperature = 22.5f,
        gravity = 1.100f,
        gravityVelocity = 0.02f,
        x = 236.0625f,
        y = 4049.375f,
        z = 1008.9375f,
        battery = 100f,
        isOG = true,
        isFG = false
    ),
    fgOrLast = RaptPillData(
        timestamp = Instant.fromEpochMilliseconds(1720265957588),
        temperature = 20.1f,
        gravity = 1.027f,
        gravityVelocity = 0.01f,
        x = 702.5f,
        y = 4067.3125f,
        z = 764.3125f,
        battery = 22.7f,
        isOG = false,
        isFG = true
    ),

    isCompleted = true
)

val testBrew2  = Brew(
    og = RaptPillData(
        timestamp = Instant.fromEpochMilliseconds(1720265957589),
        temperature = 22.5f,
        gravity = 1.100f,
        gravityVelocity = 0.02f,
        x = 236.0625f,
        y = 4049.375f,
        z = 1008.9375f,
        battery = 100f,
        isOG = true,
        isFG = false
    ),
    fgOrLast = RaptPillData(
        timestamp = Instant.fromEpochMilliseconds(1720265967588),
        temperature = 20.1f,
        gravity = 1.027f,
        gravityVelocity = 0.01f,
        x = 702.5f,
        y = 4067.3125f,
        z = 764.3125f,
        battery = 22.7f,
        isOG = false,
        isFG = true
    ),

    isCompleted = true
)