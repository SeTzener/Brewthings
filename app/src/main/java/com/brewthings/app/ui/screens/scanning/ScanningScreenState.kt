package com.brewthings.app.ui.screens.scanning

import com.brewthings.app.ble.RaptPill

data class ScanningScreenState(
    val rssiThreshold: Int = INITIAL_RSSI_THRESHOLD,
    val scanning: Boolean = false,
    val scannedInstrumentCount: Int = 0,
    val scannedInstruments: List<RaptPill> = emptyList(),
)

const val RSSI_THRESHOLD_RANGE_START = 0f
const val RSSI_THRESHOLD_RANGE_END = -200f
const val INITIAL_RSSI_THRESHOLD = -100
