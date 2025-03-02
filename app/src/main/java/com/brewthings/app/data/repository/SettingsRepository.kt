package com.brewthings.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.brewthings.app.data.model.MacAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val SELECTED_PILL = stringPreferencesKey("selected_pill")
private val IS_AUTOSAVE_ENABLED = stringPreferencesKey("is_autosave_enabled")

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
) {
    fun observeSelectedPill(): Flow<MacAddress?> = dataStore.data.map { preferences ->
        preferences[SELECTED_PILL]
    }

    suspend fun selectPill(macAddress: MacAddress) = dataStore.edit { preferences ->
        preferences[SELECTED_PILL] = macAddress
    }
}
