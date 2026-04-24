package com.forrest.elgeo.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.forrest.elgeo.ElGeoApp
import com.forrest.elgeo.MainActivity
import com.forrest.elgeo.R
import com.forrest.elgeo.data.repository.LocationRepository
import com.forrest.elgeo.data.repository.TripRepository
import com.forrest.elgeo.domain.model.AltitudeTrend
import com.forrest.elgeo.domain.model.GpsData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    companion object {
        const val EXTRA_INTERVAL = "extra_interval"
        private const val NOTIFICATION_ID = 1001
        private const val ALTITUDE_HISTORY_SIZE = 5
    }

    @Inject lateinit var locationRepository: LocationRepository
    @Inject lateinit var tripRepository: TripRepository

    private lateinit var fusedClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val altitudeHistory = mutableListOf<Double>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val interval = intent?.getLongExtra(EXTRA_INTERVAL, 1000L) ?: 1000L
        startForeground(NOTIFICATION_ID, buildNotification("Acquiring GPS signal..."))
        startLocationUpdates(interval)

        serviceScope.launch {
            tripRepository.restoreActiveTrip()
        }

        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(intervalMs: Long) {
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .setWaitForAccurateLocation(false)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val trend = computeAltitudeTrend(location.altitude)
                    val gpsData = GpsData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude,
                        speed = location.speed,
                        accuracy = location.accuracy,
                        bearing = location.bearing,
                        timestamp = location.time,
                        altitudeTrend = trend,
                        hasFix = true
                    )
                    locationRepository.updateLocation(gpsData)

                    serviceScope.launch {
                        val state = tripRepository.tripState.first()
                        if (state.isActive && !state.isPaused) {
                            tripRepository.addPoint(gpsData)
                        }
                        updateNotification(gpsData, state.isActive)
                    }
                }
            }
        }

        fusedClient.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())
    }

    private fun computeAltitudeTrend(altitude: Double): AltitudeTrend {
        altitudeHistory.add(altitude)
        if (altitudeHistory.size > ALTITUDE_HISTORY_SIZE) {
            altitudeHistory.removeAt(0)
        }
        if (altitudeHistory.size < 3) return AltitudeTrend.STABLE

        val diff = altitudeHistory.last() - altitudeHistory.first()
        return when {
            diff > 2.0 -> AltitudeTrend.RISING
            diff < -2.0 -> AltitudeTrend.FALLING
            else -> AltitudeTrend.STABLE
        }
    }

    private fun updateNotification(gpsData: GpsData, tripActive: Boolean) {
        val speedKmh = gpsData.speed * 3.6f
        val text = buildString {
            append("%.0f km/h".format(speedKmh))
            if (tripActive) append(" \u2022 Trip recording")
        }
        val notification = buildNotification(text)
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, ElGeoApp.LOCATION_CHANNEL_ID)
            .setContentTitle("ElGeo GPS")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
        serviceScope.cancel()
        super.onDestroy()
    }
}
