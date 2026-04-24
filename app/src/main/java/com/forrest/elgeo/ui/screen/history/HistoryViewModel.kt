package com.forrest.elgeo.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrest.elgeo.data.local.entity.TripEntity
import com.forrest.elgeo.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    val trips: StateFlow<List<TripEntity>> = tripRepository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteTrip(id: Long) {
        viewModelScope.launch { tripRepository.deleteTrip(id) }
    }
}
