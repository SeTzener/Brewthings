package com.brewthings.app.ui.screens.scanning

import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.ScannedRaptPill
import com.juul.kable.Bluetooth

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
)
