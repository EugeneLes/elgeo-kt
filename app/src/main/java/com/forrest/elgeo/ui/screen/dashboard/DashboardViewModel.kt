package com.forrest.elgeo.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrest.elgeo.data.repository.LocationRepository
import com.forrest.elgeo.data.repository.SettingsRepository
import com.forrest.elgeo.data.repository.TripRepository
import com.forrest.elgeo.domain.model.AppSettings
import com.forrest.elgeo.domain.model.GpsData
import com.forrest.elgeo.domain.model.TripState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    locationRepository: LocationRepository,
    private val tripRepository: TripRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val gpsData: StateFlow<GpsData> = locationRepository.gpsData

    val tripState: StateFlow<TripState> = tripRepository.tripState

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    fun startTrip() {
        viewModelScope.launch {
            val name = "Trip ${java.text.SimpleDateFormat("MMM dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}"
            tripRepository.startTrip(name)
        }
    }

    fun pauseTrip() {
        viewModelScope.launch { tripRepository.pauseTrip() }
    }

    fun resumeTrip() {
        viewModelScope.launch { tripRepository.resumeTrip() }
    }

    fun stopTrip() {
        viewModelScope.launch { tripRepository.stopTrip() }
    }
}
