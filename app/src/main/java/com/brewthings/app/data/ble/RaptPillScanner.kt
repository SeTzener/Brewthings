package com.brewthings.app.data.ble

import android.bluetooth.le.ScanSettings
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.util.Logger
import com.brewthings.app.util.asHexString
import com.juul.kable.Advertisement
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class RaptPillScanner {
    private val logger = Logger("RaptPillScanner")

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

    fun scan(): Flow<ScannedRaptPill> =
        scanner.advertisements
            .filter { advertisement ->
                advertisement.manufacturerData?.code == manufacturerId
            }
            .map { advertisement ->
                ScannedRaptPill(
                    macAddress = advertisement.address,
                    name = advertisement.name,
                    rssi = advertisement.rssi,
                    data = parseManufacturerData(advertisement),
                )
            }

    private fun parseManufacturerData(advertisement: Advertisement): RaptPillData? {
        val data = advertisement.manufacturerData?.data

        if (data == null) {
            logger.warning("Manufacturer data is null: skipping parsing.")
            return null
        }

        logger.warning("Found manufacturer data: ${data.asHexString()}.")
        return try {
            RaptPillParser.parse(data)
        } catch (t: Throwable) {
            logger.error("Manufacturer data parsing failed.", t)
            null
        }
    }
}
