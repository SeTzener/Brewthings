package com.brewthings.app.ui.screen.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScanViewModel : ViewModel(), KoinComponent {
    private val _screenState = MutableStateFlow<ScanState>(ScanState.Loading)
    val screenState: StateFlow<ScanState> = _screenState

    // This is a hack to prevent the scan from starting on navigation.
    private var isFirstLoad = true

    private val repo: RaptPillRepository by inject()

    private val logger = Logger("ScanViewModel")

    init {
        observeDevices()
    }

    private fun observeDevices() {
        _screenState.value = ScanState.Loading
        viewModelScope.launch {
            repo.observePills()
                .combine(repo.observeSelectedPill()) { pills, selected ->
                    pills to pills.find { it.macAddress == selected }
                }.collect { (pills, selectedPill) ->
                    when {
                        pills.isEmpty() -> _screenState.value = ScanState.NoDevices
                        selectedPill == null ->  _screenState.value = ScanState.NoDeviceSelected(pills)
                        else -> loadDeviceData(pills, selectedPill)
                    }
                }
        }
    }

    private suspend fun loadDeviceData(pills: List<RaptPill>, selectedPill: RaptPill) {
        
    }
}
