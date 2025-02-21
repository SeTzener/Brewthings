package com.brewthings.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
import com.brewthings.app.ui.screen.scanning.ScanningScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

val appModule = module {
    single { RaptPillDatabase.create(context = androidContext()) }
    factory { androidContext().dataStore }

    factory { RaptPillScanner() }
    factory { get<RaptPillDatabase>().raptPillDao() }
    factory { RaptPillRepository(scanner = get(), dao = get(), dataStore = get()) }
    factory { BrewsRepository(dao = get()) }

    viewModel { ScanningScreenViewModel() } // TODO(walt): Remove me
    viewModel { ScanViewModel() }
    viewModel { BrewsViewModel() }
    viewModel<GraphScreenViewModel>(qualifier = named(Destination.PILL_GRAPH)) { PillGraphScreenViewModel() }
    viewModel<GraphScreenViewModel>(qualifier = named(Destination.BREWS_GRAPH)) { BrewsGraphScreenViewModel() }
}
