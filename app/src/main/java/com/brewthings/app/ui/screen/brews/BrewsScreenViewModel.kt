package com.brewthings.app.ui.screen.brews

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.repository.BrewsRepository
import com.brewthings.app.util.Logger
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrewsScreenViewModel : ViewModel(), KoinComponent {
    var screenState: BrewsScreenState by mutableStateOf(BrewsScreenState())
        private set

    private val repo: BrewsRepository by inject()

    private val logger = Logger("BrewsScreenViewModel")

    init {
        observeBrews()
    }

    private fun observeBrews() {
        viewModelScope.launch {
            repo.observeBrews()
                .catch { error ->
                    logger.error("Failed to observe brews.", error)
                }
                .collect { brews ->
                    screenState = screenState.copy(brews = brews)
                }
        }
    }
}
