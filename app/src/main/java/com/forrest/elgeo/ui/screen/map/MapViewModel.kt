package com.forrest.elgeo.ui.screen.map

import androidx.lifecycle.ViewModel
import com.forrest.elgeo.data.repository.LocationRepository
import com.forrest.elgeo.domain.model.GpsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.views.MapView
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    locationRepository: LocationRepository
) : ViewModel() {

    val gpsData: StateFlow<GpsData> = locationRepository.gpsData

    fun downloadRegion(mapView: MapView, onProgress: (Int, Int) -> Unit, onComplete: () -> Unit) {
        val cacheManager = CacheManager(mapView)
        val minZoom = mapView.zoomLevelDouble.toInt()
        val maxZoom = (minZoom + 3).coerceAtMost(16)

        cacheManager.downloadAreaAsync(
            mapView.context,
            mapView.boundingBox,
            minZoom,
            maxZoom,
            object : CacheManager.CacheManagerCallback {
                override fun onTaskComplete() { onComplete() }
                override fun onTaskFailed(errors: Int) { onComplete() }
                override fun updateProgress(progress: Int, currentZoom: Int, zoomMin: Int, zoomMax: Int) {
                    onProgress(progress, 100)
                }
                override fun downloadStarted() {}
                override fun setPossibleTilesInArea(total: Int) {}
            }
        )
    }
}
