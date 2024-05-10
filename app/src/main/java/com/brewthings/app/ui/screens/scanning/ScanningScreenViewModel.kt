package com.brewthings.app.ui.screens.scanning

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.ble.RaptPill
import com.brewthings.app.ble.RaptPillScanner
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

private const val TAG = "ScanningScreenViewModel"

class ScanningScreenViewModel(
    private val scanner: RaptPillScanner
) : ViewModel() {
    var screenState: ScanningScreenState by mutableStateOf(ScanningScreenState())
        private set

    private var scanJob: Job? = null
    private val raptPills: MutableList<RaptPill> = mutableListOf()

    init {
        //startScan()
    }

    private fun startScan() {
        stopScan()
        raptPills.clear()
        screenState = screenState.copy(
            scannedInstruments = emptyList(),
            scanning = true,
        )
        scanJob = scanner
            .scan()
            .onEach { result ->
                Log.d(TAG, "Scanning result: $result")
                raptPills.addOrUpdate(instrument = result)
                updateInstrumentsScreenState()
            }.onCompletion {
                screenState = screenState.copy(scanning = false)
            }
            .launchIn(viewModelScope)
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
    }

    fun onRssiThresholdChanged(rssiThreshold: Int) {
        screenState = screenState.copy(rssiThreshold = rssiThreshold)
        updateInstrumentsScreenState()
    }

    private fun updateInstrumentsScreenState() {
        val filteredInstruments = raptPills
            .filter { it.rssi > screenState.rssiThreshold }

        screenState = screenState.copy(
            scannedInstrumentCount = raptPills.size,
            scannedInstruments = filteredInstruments
        )
    }

    fun onScanButtonClicked() {
        if (screenState.scanning) stopScan() else startScan()
    }

    private fun MutableList<RaptPill>.addOrUpdate(instrument: RaptPill) {
        when (val existingItemIndex = indexOfFirst { it.macAddress == instrument.macAddress }) {
            -1 -> add(instrument)
            else -> this[existingItemIndex] = instrument
        }
    }
}
