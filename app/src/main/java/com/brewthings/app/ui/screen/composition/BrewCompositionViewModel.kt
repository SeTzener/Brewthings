package com.brewthings.app.ui.screen.composition

import androidx.lifecycle.ViewModel
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.navigation.ParameterHolders.BrewComposition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent

class BrewCompositionViewModel(
    brew: Brew = BrewComposition.brew ?: error("brew is required"),
) : ViewModel(), KoinComponent {
    private val _screenState = MutableStateFlow(
        BrewCompositionScreenState(
            abvPercentage = brew.abvPercentage,
            sweetnessPercentage = brew.sweetnessPercentage,
        ),
    )
    val screenState: StateFlow<BrewCompositionScreenState> = _screenState
}

private val Brew.abvPercentage: Float get() = (abv * 100f).coerceIn(0f, 100f)

private val Brew.sweetnessPercentage: Float get() {
    val sweetness = ((fgOrLast.gravity - fgOrLast.gravity) / (og.gravity - fgOrLast.gravity))
    return (sweetness * 100f).coerceIn(0f, 100f)
}
