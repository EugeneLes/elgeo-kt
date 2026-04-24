package com.forrest.elgeo.data.repository

import android.location.Location
import com.forrest.elgeo.data.local.dao.LocationPointDao
import com.forrest.elgeo.data.local.dao.TripDao
import com.forrest.elgeo.data.local.entity.LocationPointEntity
import com.forrest.elgeo.data.local.entity.TripEntity
import com.forrest.elgeo.domain.model.GpsData
import com.forrest.elgeo.domain.model.TripState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val locationPointDao: LocationPointDao
) {
    private val _tripState = MutableStateFlow(TripState())
    val tripState: StateFlow<TripState> = _tripState.asStateFlow()

    private var lastLocation: Location? = null

    val allTrips: Flow<List<TripEntity>> = tripDao.getAllTrips()

    suspend fun startTrip(name: String): Long {
        val now = System.currentTimeMillis()
        val entity = TripEntity(name = name, startTime = now, status = "active")
        val id = tripDao.insert(entity)
        lastLocation = null
        _tripState.value = TripState(
            isActive = true,
            tripId = id,
            startTime = now
        )
        return id
    }

    suspend fun pauseTrip() {
        val state = _tripState.value
        if (state.isActive && !state.isPaused) {
            _tripState.value = state.copy(
                isPaused = true,
                pauseTime = System.currentTimeMillis()
            )
        }
    }

    suspend fun resumeTrip() {
        val state = _tripState.value
        if (state.isActive && state.isPaused) {
            val pauseDuration = System.currentTimeMillis() - state.pauseTime
            _tripState.value = state.copy(
                isPaused = false,
                totalPauseDuration = state.totalPauseDuration + pauseDuration
            )
        }
    }

    suspend fun stopTrip() {
        val state = _tripState.value
        val tripId = state.tripId ?: return
        val now = System.currentTimeMillis()

        tripDao.getTripById(tripId)?.let { entity ->
            tripDao.update(
                entity.copy(
                    endTime = now,
                    distance = state.distance,
                    maxSpeed = state.maxSpeed,
                    avgSpeed = state.avgSpeed,
                    status = "completed"
                )
            )
        }

        lastLocation = null
        _tripState.value = TripState()
    }

    suspend fun addPoint(gpsData: GpsData) {
        val state = _tripState.value
        val tripId = state.tripId ?: return
        if (!state.isActive || state.isPaused) return

        val point = LocationPointEntity(
            tripId = tripId,
            latitude = gpsData.latitude,
            longitude = gpsData.longitude,
            altitude = gpsData.altitude,
            speed = gpsData.speed,
            accuracy = gpsData.accuracy,
            bearing = gpsData.bearing,
            timestamp = gpsData.timestamp
        )
        locationPointDao.insert(point)

        var addedDistance = 0.0
        val current = Location("").apply {
            latitude = gpsData.latitude
            longitude = gpsData.longitude
        }
        lastLocation?.let { last ->
            addedDistance = last.distanceTo(current).toDouble()
        }
        lastLocation = current

        val now = System.currentTimeMillis()
        val activeDuration = now - state.startTime - state.totalPauseDuration

        _tripState.value = state.copy(
            distance = state.distance + addedDistance,
            duration = activeDuration,
            maxSpeed = maxOf(state.maxSpeed, gpsData.speed),
            speedSum = state.speedSum + gpsData.speed,
            pointCount = state.pointCount + 1
        )
    }

    suspend fun getTripById(id: Long): TripEntity? = tripDao.getTripById(id)

    suspend fun getTripPoints(tripId: Long): List<LocationPointEntity> =
        locationPointDao.getPointsForTrip(tripId)

    suspend fun deleteTrip(id: Long) {
        tripDao.deleteById(id)
    }

    suspend fun restoreActiveTrip() {
        val active = tripDao.getActiveTrip()
        if (active != null) {
            val pointCount = locationPointDao.getPointCount(active.id)
            _tripState.value = TripState(
                isActive = true,
                tripId = active.id,
                distance = active.distance,
                startTime = active.startTime,
                pointCount = pointCount
            )
        }
    }
}
