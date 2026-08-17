package com.brewthings.app.ui.screen.brews

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.repository.BrewsRepository
import com.brewthings.app.data.repository.SettingsRepository
import com.brewthings.app.util.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrewsViewModel : ViewModel(), KoinComponent {
    var screenState: BrewsState by mutableStateOf(BrewsState())
        private set

    private val repo: BrewsRepository by inject()
    private val settings: SettingsRepository by inject()

    private val logger = Logger("BrewsScreenViewModel")

    init {
        observeBrews()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeBrews() {
        viewModelScope.launch {
            settings.observeSelectedPill()
                .flatMapLatest { macAddress ->
                    macAddress?.let { repo.observeBrews(it) } ?: flowOf(emptyList())
                }
                .catch { error ->
                    logger.error("Failed to observe brews.", error)
                }
                .collect { brews ->
                    screenState = screenState.copy(brews = brews)
                }
        }
    }
}
