package com.brewthings.app.ui

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.brewthings.app.ui.screens.scanning.ScanningScreen
import com.brewthings.app.ui.theme.BrewthingsTheme

object RequestCode {
    const val EnableBluetooth = 55001
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrewthingsTheme {
                ScanningScreen(
                    openAppDetails = ::openAppDetails,
                    showLocationSettings = ::showLocationSettings,
                    enableBluetooth = ::enableBluetooth
                )
            }
        }
    }

    /** @throws SecurityException if [BLUETOOTH_CONNECT] permission has not been granted on Android 12 (API 31) or newer. */
    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun enableBluetooth() {
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), RequestCode.EnableBluetooth)
    }

    private fun showLocationSettings() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun openAppDetails() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        })
    }

}
