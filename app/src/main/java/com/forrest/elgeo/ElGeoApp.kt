package com.forrest.elgeo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class ElGeoApp : Application() {

    companion object {
        const val LOCATION_CHANNEL_ID = "location_tracking"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = filesDir
            osmdroidTileCache = cacheDir.resolve("osmdroid")
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            LOCATION_CHANNEL_ID,
            "Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows GPS tracking status"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
