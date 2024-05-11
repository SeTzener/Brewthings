package com.brewthings.app.di

import com.brewthings.app.ble.RaptPillScanner
import com.brewthings.app.ui.screens.scanning.ScanningScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { RaptPillScanner() }
    viewModel { ScanningScreenViewModel(scanner = get()) }
}
