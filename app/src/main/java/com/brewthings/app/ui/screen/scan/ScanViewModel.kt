@file:OptIn(ExperimentalCoroutinesApi::class)

package com.brewthings.app.ui.screen.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.data.domain.BluetoothScanState.Unavailable.Reason
import com.brewthings.app.data.domain.BrewMeasurements
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.Measurement
import com.brewthings.app.data.domain.SensorMeasurements
import com.brewthings.app.data.domain.SensorReadings
import com.brewthings.app.data.domain.toBluetoothScanState
import com.brewthings.app.data.model.Brew
import com.brewthings.app.data.model.MacAddress
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.repository.BrewsRepository
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.util.calculateABV
import com.brewthings.app.util.datetime.TimeRange
import com.juul.kable.Bluetooth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScanViewModel : ViewModel(), KoinComponent {
    // Dependencies
    private val pills: RaptPillRepository by inject()
    private val brews: BrewsRepository by inject()

    private val bluetoothIsScanning = MutableStateFlow(false)

    private var latestScanResult: ScannedRaptPill? = null

    val devices: StateFlow<List<Device>> = pills.observePills()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val selectedMacAddress: Flow<MacAddress?> = pills.observeSelectedPill()

    val selectedDevice: StateFlow<Device?> = devices
        .combine(selectedMacAddress) { devices, selectedMacAddress ->
            devices.find { it.macAddress == selectedMacAddress }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val bluetoothAvailability: Flow<Bluetooth.Availability> = Bluetooth.availability

    val bluetoothScanState: StateFlow<BluetoothScanState> = bluetoothAvailability
        .combine(bluetoothIsScanning) { availability, isScanning ->
            availability.toBluetoothScanState(isScanning)
        }.stateIn(viewModelScope, SharingStarted.Lazily, BluetoothScanState.Unavailable(reason = Reason.Unknown))

    private val savedReadings: Flow<SensorReadings?> = selectedMacAddress
        .flatMapLatest { selected: MacAddress? ->
            selected?.let {
                pills.observeLatestData(it)
            } ?: flowOf(null)
        }

    private val scannedReadings: Flow<SensorReadings?> = bluetoothScanState
        .flatMapLatest { scan ->
            val scanResults = if (scan == BluetoothScanState.InProgress) {
                flowOf(null) // start with null, so it doesn't block the observer until something is found
                    .flatMapLatest { pills.fromBluetooth() }
                    .onEach { latestScanResult = it }
            } else flowOf(latestScanResult)

            scanResults.map { it?.data }
        }

    val sensorMeasurements: StateFlow<SensorMeasurements> = scannedReadings
        .combine(savedReadings) { scanned, saved ->
            if (scanned == null) {
                if (saved != null) {
                    createSensorMeasurements(latest = saved, previous = null)
                } else {
                    emptyList()
                }
            } else {
                createSensorMeasurements(latest = scanned, previous = saved)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val savedCurrentBrew: Flow<Brew?> = selectedMacAddress
        .flatMapLatest { selected: MacAddress? ->
            selected?.let {
                brews.observeCurrentBrew(it)
            } ?: flowOf(null)
        }

    val brewMeasurements: StateFlow<BrewMeasurements?> = scannedReadings
        .combine(savedCurrentBrew) { scanned, brew ->
            if (brew == null) {
                null
            } else {
                val latest = scanned ?: brew.fgOrLast
                createBrewMeasurements(
                    latest = latest,
                    previous = brew.fgOrLast.takeIf { it != latest },
                    og = brew.og,
                    feedings = brew.feedings
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val canSave: StateFlow<Boolean> = scannedReadings
        .combine(savedReadings) { scanned, saved ->
            if (scanned == null) false else SensorReadings.compare(scanned, saved) != 0
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Functions
    fun save() {
        viewModelScope.launch {
            latestScanResult?.also {
                pills.save(it)
            }
        }
    }

    fun toggleScan(scanState: BluetoothScanState) {
        when (scanState) {
            BluetoothScanState.InProgress -> startScan()
            BluetoothScanState.Idle -> stopScan()
            is BluetoothScanState.Unavailable -> {
                // NO-OP
            }
        }
    }

    private fun startScan() {
        bluetoothIsScanning.value = true
    }

    private fun stopScan() {
        bluetoothIsScanning.value = false
    }
}

private fun createSensorMeasurements(latest: SensorReadings, previous: SensorReadings?): SensorMeasurements =
    listOfNotNull(
        Measurement(DataType.GRAVITY, latest.gravity, previous?.gravity),
        Measurement(DataType.TEMPERATURE, latest.temperature, previous?.temperature),
        latest.gravityVelocity?.let { Measurement(DataType.VELOCITY_MEASURED, it, previous?.gravityVelocity) },
        Measurement(DataType.BATTERY, latest.battery, previous?.battery),
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
