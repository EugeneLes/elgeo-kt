package com.forrest.elgeo.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.forrest.elgeo.domain.model.AltitudeUnit
import com.forrest.elgeo.domain.model.AppSettings
import com.forrest.elgeo.domain.model.SpeedUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "elgeo_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val SPEED_UNIT = stringPreferencesKey("speed_unit")
        val ALTITUDE_UNIT = stringPreferencesKey("altitude_unit")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val GPS_UPDATE_INTERVAL = longPreferencesKey("gps_update_interval")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            speedUnit = prefs[Keys.SPEED_UNIT]?.let { SpeedUnit.valueOf(it) } ?: SpeedUnit.KMH,
            altitudeUnit = prefs[Keys.ALTITUDE_UNIT]?.let { AltitudeUnit.valueOf(it) } ?: AltitudeUnit.METERS,
            keepScreenOn = prefs[Keys.KEEP_SCREEN_ON] ?: true,
            darkMode = prefs[Keys.DARK_MODE] ?: true,
            gpsUpdateIntervalMs = prefs[Keys.GPS_UPDATE_INTERVAL] ?: 1000L
        )
    }

    suspend fun updateSpeedUnit(unit: SpeedUnit) {
        context.dataStore.edit { it[Keys.SPEED_UNIT] = unit.name }
    }

    suspend fun updateAltitudeUnit(unit: AltitudeUnit) {
        context.dataStore.edit { it[Keys.ALTITUDE_UNIT] = unit.name }
    }

    suspend fun updateKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KEEP_SCREEN_ON] = enabled }
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun updateGpsInterval(intervalMs: Long) {
        context.dataStore.edit { it[Keys.GPS_UPDATE_INTERVAL] = intervalMs }
    }
}
