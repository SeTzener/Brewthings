package com.brewthings.app.ui.screens.scanning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.util.Logger
import com.juul.kable.Bluetooth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScanningScreenViewModel : ViewModel(), KoinComponent {
    // This is a hack to prevent the scan from starting on navigation.
    private var isFirstLoad = true

    var screenState: ScanningScreenState by mutableStateOf(ScanningScreenState())
        private set

    private val repo: RaptPillRepository by inject()

    private var scanJob: Job? = null
    private val scannedRaptPills: MutableList<ScannedRaptPill> = mutableListOf()

    private val logger = Logger("ScanningScreenViewModel")

    init {
        observeBluetoothAvailability()
        observeDatabase()
    }

    fun savePill(scannedRaptPill: ScannedRaptPill) {
        viewModelScope.launch {
            repo.save(scannedRaptPill)
        }
    }

    fun onFirstLoad() {
        if (isFirstLoad) {
            logger.info("Starting scan on first load.")
            toggleScan()
            isFirstLoad = false
        }
    }

    fun setIsOG(macAddress: String, timestamp: Instant, isOg: Boolean){
        viewModelScope.launch {
            repo.setIsOG(macAddress = macAddress, timestamp = timestamp, isOg = isOg)
        }
    }

    fun setIsFG(macAddress: String, timestamp: Instant, isOg: Boolean){
        viewModelScope.launch {
            repo.setIsFG(macAddress = macAddress, timestamp = timestamp, isOg = isOg)
        }
    }

    fun toggleScan() {
        when {
            screenState.bluetooth is Bluetooth.Availability.Available && !screenState.scanning -> startScan()
            screenState.bluetooth !is Bluetooth.Availability.Available || screenState.scanning -> stopScan()
        }
    }

    fun onRssiThresholdChanged(rssiThreshold: Int) {
        screenState = screenState.copy(rssiThreshold = rssiThreshold)
        updateInstrumentsScreenState()
    }

    private fun observeDatabase() {
        repo.fromDatabase()
            .onEach { raptPills ->
                screenState = screenState.copy(savedPills = raptPills)
            }
            .launchIn(viewModelScope)
    }

    private fun observeBluetoothAvailability() {
        Bluetooth.availability
            .onEach { availability ->
                screenState = screenState.copy(bluetooth = availability)
            }
            .launchIn(viewModelScope)
    }

    private fun startScan() {
        stopScan()
        scannedRaptPills.clear()
        screenState = screenState.copy(
            scannedPills = emptyList(),
            scanning = true,
        )
        scanJob = repo
            .fromBluetooth()
            .onEach { result ->
                logger.info("Scanning result: $result")
                scannedRaptPills.addOrUpdate(instrument = result)
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

    private fun updateInstrumentsScreenState() {
        val filteredInstruments = scannedRaptPills
            .filter { it.rssi > screenState.rssiThreshold }

        screenState = screenState.copy(
            scannedPillsCount = scannedRaptPills.size,
            scannedPills = filteredInstruments
        )
    }

    private fun MutableList<ScannedRaptPill>.addOrUpdate(instrument: ScannedRaptPill) {
        when (val existingItemIndex = indexOfFirst { it.macAddress == instrument.macAddress }) {
            -1 -> add(instrument)
            else -> this[existingItemIndex] = instrument
        }
    }

    fun onPillUpdate(raptPill: RaptPill) {
        viewModelScope.launch {
            repo.updatePill(raptPill = raptPill)
        }
    }
}
