package com.brewthings.app.di

import androidx.room.Room
import com.brewthings.app.data.ble.RaptPillScanner
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.data.storage.RaptPillDatabase
import com.brewthings.app.ui.screens.scanning.ScanningScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { RaptPillScanner() }
    single {
        Room.databaseBuilder(
            androidContext(),
            RaptPillDatabase::class.java, "rapt-db"
        ).build()
    }
    factory { get<RaptPillDatabase>().raptPillDao() }
    factory { RaptPillRepository(scanner = get(), dao = get()) }
    viewModel { ScanningScreenViewModel(repo = get()) }
}
