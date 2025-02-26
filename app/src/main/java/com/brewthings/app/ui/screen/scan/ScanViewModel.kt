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
    private var latestScanResult: ScannedRaptPill? = null

    private val _isBluetoothScanning = MutableStateFlow(false)
    val isBluetoothScanning: StateFlow<Boolean> = _isBluetoothScanning

    val devices: StateFlow<List<Device>> = pills.observePills()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val selectedMacAddress: Flow<MacAddress?> = pills.observeSelectedPill()

    val selectedDevice: StateFlow<Device?> = devices
        .combine(selectedMacAddress) { devices, selectedMacAddress ->
            devices.find { it.macAddress == selectedMacAddress }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val scannedReadings: Flow<SensorReadings?> = isBluetoothScanning
        .flatMapLatest { isScanning ->
            val scanResults = if (isScanning) {
                flowOf(null) // start with null, so it doesn't block the observer until something is found
                    .flatMapLatest { pills.fromBluetooth() }
                    .onEach { latestScanResult = it }
            } else flowOf(latestScanResult)

            scanResults.map { it?.data }
        }

    private val currentBrew: Flow<Brew?> = selectedMacAddress
        .flatMapLatest { selected: MacAddress? ->
            selected?.let { macAddress ->
                flowOf(null) // start with null, so it doesn't block the observer until something is found
                    .flatMapLatest { brews.observeCurrentBrew(macAddress) }
            } ?: flowOf(null)
        }

    val hasBrew: StateFlow<Boolean> = currentBrew
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val lastUpdate: StateFlow<Instant?> = scannedReadings
        .combine(currentBrew) { scanned, brew ->
            when {
                scanned == null && brew != null -> brew.fgOrLast.timestamp
                scanned != null -> scanned.timestamp
                else -> null
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val sensorMeasurements: StateFlow<SensorMeasurements> =
        brewWithLatestAndPrevious(default = emptyList()) { _, latest, previous ->
            createSensorMeasurements(latest, previous)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val brewWithMeasurements: StateFlow<BrewWithMeasurements?> =
        brewWithLatestAndPrevious(default = null) { brew, latest, previous ->
            brew?.let {
                BrewWithMeasurements(
                    brew = brew,
                    measurements = createBrewMeasurements(
                        latest = latest,
                        previous = previous,
                        og = brew.og,
                        feedings = brew.feedings,
                    )
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val canSave: StateFlow<Boolean> = scannedReadings
        .map { result -> latestScanResult?.data != result }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Functions
    init {
        isBluetoothScanning
            .onEach { isScanning -> logger.info("Bluetooth scanning is ${if (isScanning) "on" else "off"}.") }
            .launchIn(viewModelScope)
    }

    fun save() {
        viewModelScope.launch {
            latestScanResult?.also {
                pills.save(it)
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

    private fun <T> brewWithLatestAndPrevious(
        default: T,
        callback: (brew: Brew?, SensorReadings, SensorReadings?) -> T,
    ): Flow<T> = scannedReadings
        .flatMapLatest { scanned ->
            if (scanned != null) {
                currentBrew.map { brew ->
                    val previous = brew?.fgOrLast
                    callback(brew, scanned, previous)
                }
            } else {
                currentBrew.flatMapLatest { brew ->
                    brew?.let {
                        brews.observeBrewData(it)
                            .map { data ->
                                if (data.isNotEmpty()) {
                                    val latest = data.last()
                                    val previous = data.dropLast(1).lastOrNull()
                                    callback(brew, latest, previous)
                                } else default
                            }
                    } ?: flowOf(default)
                }
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
