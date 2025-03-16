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
import com.brewthings.app.ui.navigation.MainNavGraph
import com.brewthings.app.ui.theme.BrewthingsTheme

private const val REQUEST_CODE_ENABLE_BLUETOOTH = 55001

interface ActivityCallbacks {
    fun enableBluetooth()
    fun showLocationSettings()
    fun openAppDetails()
}

class MainActivity : ComponentActivity(), ActivityCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrewthingsTheme {
                MainNavGraph(activityCallbacks = this)
            }
        }
    }

    /** @throws SecurityException if [BLUETOOTH_CONNECT] permission has not been granted on Android 12 (API 31) or newer. */
    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    override fun enableBluetooth() {
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_ENABLE_BLUETOOTH)
    }

    override fun showLocationSettings() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun openAppDetails() {
        startActivity(
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            },
        )
    }
}
