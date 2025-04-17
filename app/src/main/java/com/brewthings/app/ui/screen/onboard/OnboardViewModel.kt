@file:OptIn(ExperimentalCoroutinesApi::class)

package com.brewthings.app.ui.screen.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.data.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OnboardViewModel : ViewModel(), KoinComponent {
    private val settings: SettingsRepository by inject()
    private val pills: RaptPillRepository by inject()

    private val _isBluetoothScanning = MutableStateFlow(false)
    val isBluetoothScanning: StateFlow<Boolean> = _isBluetoothScanning

    private val _isDone = MutableStateFlow(false)
    val isDone: StateFlow<Boolean> = _isDone

    private val latestScannedResult: StateFlow<ScannedRaptPill?> = isBluetoothScanning
        .flatMapLatest { scanning ->
            if (scanning) {
                pills.fromBluetooth()
            } else {
                flowOf()
            }
        }
        .combine(
            pills.observePills()
        ) { scanned, saved ->
            if (saved.firstOrNull { it.macAddress == scanned.macAddress } != null) {
                null
            } else {
                scanned
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            latestScannedResult
                .filterNotNull()
                .collect { pill ->
                    pills.save(pill)
                    settings.selectPill(pill.macAddress)
                    _isDone.value = true
                }
        }
    }

    fun startScan() {
        _isBluetoothScanning.value = true
    }

    fun stopScan() {
        _isBluetoothScanning.value = false
    }
}