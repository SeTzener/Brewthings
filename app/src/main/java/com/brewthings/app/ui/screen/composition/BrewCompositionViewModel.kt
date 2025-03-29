package com.brewthings.app.ui.screen.composition

import androidx.lifecycle.ViewModel
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.navigation.ParameterHolders.BrewComposition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import kotlin.math.pow

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

private val Brew.abvPercentage: Float get() = abv.coerceIn(0f, 100f)

/**
 * We calculate the residual sugars by converting the alcohol-adjusted final gravity into Brix.
 * We use a standard SG to Brix conversion, and then multiply again by the FG, as described at
 * [Vinolab](https://www.vinolab.hr/calculator/gravity-density-sugar-conversions-en19).
 */
private val Brew.sweetnessPercentage: Float get() {
    val fg = fgOrLast.gravity
    val brix = 143.254f * fg.pow(3) - 648.670f * fg.pow(2) + 1125.805f * fg - 620.389f
    val sweetness = brix * fg
    return sweetness.coerceIn(0f, 100f)
}
