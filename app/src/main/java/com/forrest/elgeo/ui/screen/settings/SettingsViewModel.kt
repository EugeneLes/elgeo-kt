package com.forrest.elgeo.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrest.elgeo.data.repository.SettingsRepository
import com.forrest.elgeo.domain.model.AltitudeUnit
import com.forrest.elgeo.domain.model.AppSettings
import com.forrest.elgeo.domain.model.SpeedUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    fun setSpeedUnit(unit: SpeedUnit) {
        viewModelScope.launch { settingsRepository.setSpeedUnit(unit) }
    }

    fun setAltitudeUnit(unit: AltitudeUnit) {
        viewModelScope.launch { settingsRepository.setAltitudeUnit(unit) }
    }

    fun setKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setKeepScreenOn(enabled) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkMode(enabled) }
    }

    fun setGpsInterval(intervalMs: Long) {
        viewModelScope.launch { settingsRepository.setGpsInterval(intervalMs) }
    }
}
