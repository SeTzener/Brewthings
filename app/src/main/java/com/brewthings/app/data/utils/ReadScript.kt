package com.brewthings.app.data.utils

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

internal object ReadScript {
    @Throws(IOException::class)
    fun insertFromFile(context: Context, resourceCode: Int, db: SupportSQLiteDatabase): Int {
        // Resetting Counter
        var result = 0

        // Open the resource
        val insertsStream = context.resources.openRawResource(resourceCode)
        val insertReader = BufferedReader(InputStreamReader(insertsStream))

        // Iterate through lines (assuming each insert has its own line and theres no other stuff)
        while (insertReader.ready()) {
            val insertStmt = insertReader.readLine()
            if (insertStmt != null) {
                if (insertStmt.isEmpty()) continue
                db.execSQL(insertStmt)
                result++
            }
        }
        insertReader.close()

        // returning number of inserted rows
        return result
    }
}
