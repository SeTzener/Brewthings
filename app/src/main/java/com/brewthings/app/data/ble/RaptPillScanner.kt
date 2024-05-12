package com.brewthings.app.data.ble

import android.bluetooth.le.ScanSettings
import com.brewthings.app.data.model.RaptPill
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class RaptPillScanner {
    // 16722 = 0x52 0x41 prefix in little endian = `R` `A` = start of "RAPT"
    private val manufacturerId: Int = 16722

    private val scanner = Scanner {
        logging {
            engine = SystemLogEngine
            level = Logging.Level.Warnings
            format = Logging.Format.Multiline
        }
        scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

    }

    fun scan(): Flow<RaptPill> =
        scanner.advertisements
            .filter { advertisement ->
                advertisement.manufacturerData?.code == manufacturerId
            }
            .map { advertisement ->
                RaptPill(
                    macAddress = advertisement.address,
                    name = advertisement.name,
                    rssi = advertisement.rssi,
                    manufacturerData = advertisement.manufacturerData?.data
                )
            }
}
