@file:OptIn(ExperimentalCoroutinesApi::class)

package com.brewthings.app.ui.screen.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.domain.BrewMeasurements
import com.brewthings.app.data.domain.BrewWithMeasurements
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.Measurement
import com.brewthings.app.data.domain.SensorMeasurements
import com.brewthings.app.data.domain.SensorReadings
import com.brewthings.app.data.model.Brew
import com.brewthings.app.data.model.MacAddress
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.repository.BrewsRepository
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.util.Logger
import com.brewthings.app.util.calculateABV
import com.brewthings.app.util.datetime.TimeRange
import com.brewthings.app.util.toPercent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScanViewModel : ViewModel(), KoinComponent {
    // Dependencies
    private val pills: RaptPillRepository by inject()
    private val brews: BrewsRepository by inject()

    private val logger = Logger("ScanViewModel")

    // State & Flows
    private val _isBluetoothScanning = MutableStateFlow(false)
    val isBluetoothScanning: StateFlow<Boolean> = _isBluetoothScanning

    private val latestSavedResult = MutableStateFlow<ScannedRaptPill?>(null)

    private val latestScannedResult: StateFlow<ScannedRaptPill?> = isBluetoothScanning
        .flatMapLatest { scanning ->
            if (scanning) {
                pills.fromBluetooth()
            } else {
                flowOf()
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val devices: StateFlow<List<Device>> = pills.observePills()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val selectedMacAddress: Flow<MacAddress?> = pills.observeSelectedPill()

    val selectedDevice: StateFlow<Device?> = devices
        .combine(selectedMacAddress) { devices, selectedMacAddress ->
            devices.find { it.macAddress == selectedMacAddress }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val currentBrew: Flow<Brew?> = selectedMacAddress
        .flatMapLatest { selected: MacAddress? ->
            selected?.let { macAddress ->
                brews.observeCurrentBrew(macAddress)
            } ?: flowOf(null)
        }

    val hasData: StateFlow<Boolean> = latestScannedResult
        .combine(currentBrew) { scanned, brew ->
            scanned != null || brew != null
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val lastUpdate: StateFlow<Instant?> = latestScannedResult
        .combine(currentBrew) { scanned, brew ->
            when {
                scanned == null && brew != null -> brew.fgOrLast.timestamp
                scanned != null -> scanned.data.timestamp
                else -> null
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val latestUnsavedResult: StateFlow<ScannedRaptPill?> = latestSavedResult
        .combine(latestScannedResult) { latest, scanned ->
            scanned?.takeIf { latest != scanned }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val brewWithLatestAndPrevious: Flow<BrewWithLatestAndPrevious?> = latestUnsavedResult
        .flatMapLatest { scanned ->
        if (scanned != null) {
            currentBrew.map { brew ->
                val previous = brew?.fgOrLast
                BrewWithLatestAndPrevious(brew, scanned.data, previous)
            }
        } else {
            currentBrew.flatMapLatest { brew ->
                brew?.let {
                    brews.observeBrewData(it)
                        .map { data ->
                            if (data.isNotEmpty()) {
                                val latest = data.last()
                                val previous = data.dropLast(1).lastOrNull()
                                BrewWithLatestAndPrevious(brew, latest, previous)
                            } else null
                        }
                } ?: flowOf(null)
            }
        }
    }

    val sensorMeasurements: StateFlow<SensorMeasurements> = brewWithLatestAndPrevious
        .map { data ->
            data?.run { createSensorMeasurements(latest, previous) } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val brewWithMeasurements: StateFlow<BrewWithMeasurements?> = brewWithLatestAndPrevious
        .map { data ->
            data?.run {
                brew?.let {
                    BrewWithMeasurements(
                        brew = it,
                        measurements = createBrewMeasurements(
                            latest = latest,
                            previous = previous,
                            og = it.og,
                            feedings = it.feedings,
                        )
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val canSave: StateFlow<Boolean> = latestUnsavedResult
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Functions
    init {
        isBluetoothScanning
            .onEach { isScanning -> logger.info("Bluetooth scanning is ${if (isScanning) "on" else "off"}.") }
            .launchIn(viewModelScope)
    }

    fun save() {
        viewModelScope.launch {
            latestScannedResult.value?.also { result ->
                pills.save(result)
                latestSavedResult.value = result
            }
        }
    }

    fun startScan() {
        _isBluetoothScanning.value = true
    }

    fun stopScan() {
        _isBluetoothScanning.value = false
    }

    fun selectDevice(device: Device) {
        viewModelScope.launch {
            pills.selectPill(device.macAddress)
        }
    }
}

private fun createSensorMeasurements(latest: SensorReadings, previous: SensorReadings?): SensorMeasurements =
    listOfNotNull(
        Measurement(DataType.GRAVITY, latest.gravity, previous?.gravity),
        Measurement(DataType.TEMPERATURE, latest.temperature, previous?.temperature),
        latest.gravityVelocity?.let { Measurement(DataType.VELOCITY_MEASURED, it, previous?.gravityVelocity) },
        Measurement(DataType.BATTERY, latest.battery.toPercent(), previous?.battery?.toPercent()),
    )

private fun createBrewMeasurements(
    latest: SensorReadings,
    previous: SensorReadings?,
    og: SensorReadings,
    feedings: List<Float>,
): BrewMeasurements =
    BrewMeasurements(
        timeRange = TimeRange(og.timestamp, latest.timestamp),
        measurements = listOfNotNull(
            Measurement(
                dataType = DataType.ABV,
                value = calculateABV(og = og.gravity, fg = latest.gravity, feedings = feedings) ?: 0f,
                previousValue = previous?.let {
                    calculateABV(og = og.gravity, fg = it.gravity, feedings = feedings)
                }
            )
        ),
    )

private data class BrewWithLatestAndPrevious(val brew: Brew?, val latest: SensorReadings, val previous: SensorReadings?)
