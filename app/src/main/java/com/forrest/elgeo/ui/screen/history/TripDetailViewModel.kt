package com.forrest.elgeo.ui.screen.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrest.elgeo.data.local.entity.LocationPointEntity
import com.forrest.elgeo.data.local.entity.TripEntity
import com.forrest.elgeo.data.repository.TripRepository
import com.forrest.elgeo.utils.ExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripDetailState(
    val trip: TripEntity? = null,
    val points: List<LocationPointEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripDetailState())
    val state: StateFlow<TripDetailState> = _state.asStateFlow()

    fun loadTrip(tripId: Long) {
        viewModelScope.launch {
            val trip = tripRepository.getTripById(tripId)
            val points = tripRepository.getTripPoints(tripId)
            _state.value = TripDetailState(trip = trip, points = points, isLoading = false)
        }
    }

    fun exportGpx(context: Context) {
        val s = _state.value
        val trip = s.trip ?: return
        viewModelScope.launch {
            val file = ExportUtils.exportGpx(context, trip, s.points)
            ExportUtils.shareFile(context, file, "application/gpx+xml")
        }
    }

    fun exportCsv(context: Context) {
        val s = _state.value
        val trip = s.trip ?: return
        viewModelScope.launch {
            val file = ExportUtils.exportCsv(context, trip, s.points)
            ExportUtils.shareFile(context, file, "text/csv")
        }
    }
}
