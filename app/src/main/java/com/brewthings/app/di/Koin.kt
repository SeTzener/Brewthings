package com.brewthings.app.di

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.repository.BrewsRepository
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.data.storage.RaptPillDatabase
import com.brewthings.app.ui.navigation.legacy.Destination
import com.brewthings.app.ui.screen.brews.BrewsViewModel
import com.brewthings.app.ui.screen.graph.BrewsGraphScreenViewModel
import com.brewthings.app.ui.screen.graph.GraphScreenViewModel
import com.brewthings.app.ui.screen.graph.PillGraphScreenViewModel
import com.brewthings.app.ui.screen.scan.ScanViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { RaptPillDatabase.create(context = androidContext()) }

    factory { RaptPillScanner() }
    factory { get<RaptPillDatabase>().raptPillDao() }
    factory { RaptPillRepository(scanner = get(), dao = get()) }
    factory { BrewsRepository(dao = get()) }

    // viewModel { ScanningScreenViewModel() }
    viewModel { ScanViewModel() }
    viewModel { BrewsViewModel() }
    viewModel<GraphScreenViewModel>(qualifier = named(Destination.PILL_GRAPH)) { PillGraphScreenViewModel() }
    viewModel<GraphScreenViewModel>(qualifier = named(Destination.BREWS_GRAPH)) { BrewsGraphScreenViewModel() }
}
