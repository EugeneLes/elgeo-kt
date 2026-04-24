package com.forrest.elgeo.data.repository

import com.forrest.elgeo.data.local.datastore.SettingsDataStore
import com.forrest.elgeo.domain.model.AltitudeUnit
import com.forrest.elgeo.domain.model.AppSettings
import com.forrest.elgeo.domain.model.SpeedUnit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: SettingsDataStore
) {
    val settings: Flow<AppSettings> = dataStore.settings

    suspend fun setSpeedUnit(unit: SpeedUnit) = dataStore.updateSpeedUnit(unit)
    suspend fun setAltitudeUnit(unit: AltitudeUnit) = dataStore.updateAltitudeUnit(unit)
    suspend fun setKeepScreenOn(enabled: Boolean) = dataStore.updateKeepScreenOn(enabled)
    suspend fun setDarkMode(enabled: Boolean) = dataStore.updateDarkMode(enabled)
    suspend fun setGpsInterval(intervalMs: Long) = dataStore.updateGpsInterval(intervalMs)
}
