package com.brewthings.app.di

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.repository.BrewsRepository
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.data.storage.RaptPillDatabase
import com.brewthings.app.ui.screen.brews.BrewsScreenViewModel
import com.brewthings.app.ui.screen.graph.GraphScreenViewModel
import com.brewthings.app.ui.screen.scanning.ScanningScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { RaptPillDatabase.create(context = androidContext()) }

    factory { RaptPillScanner() }
    factory { get<RaptPillDatabase>().raptPillDao() }
    factory { RaptPillRepository(scanner = get(), dao = get()) }
    factory { BrewsRepository(dao = get()) }

    viewModel { ScanningScreenViewModel() }
    viewModel { BrewsScreenViewModel() }
    viewModel { GraphScreenViewModel() }
}
