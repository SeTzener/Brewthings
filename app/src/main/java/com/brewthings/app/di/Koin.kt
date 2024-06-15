package com.brewthings.app.di

import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.data.storage.RaptPillDatabase
import com.brewthings.app.ui.screens.graph.GraphScreenViewModel
import com.brewthings.app.ui.screens.scanning.ScanningScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { RaptPillScanner() }
    single { RaptPillDatabase.create(context = androidContext()) }
    factory { get<RaptPillDatabase>().raptPillDao() }
    factory { RaptPillRepository(scanner = get(), dao = get()) }
    viewModel { ScanningScreenViewModel(repo = get()) }
    viewModel { GraphScreenViewModel(repo = get()) }
}
