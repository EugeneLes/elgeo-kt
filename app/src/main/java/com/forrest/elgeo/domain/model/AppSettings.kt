package com.forrest.elgeo.domain.model

data class AppSettings(
    val speedUnit: SpeedUnit = SpeedUnit.KMH,
    val altitudeUnit: AltitudeUnit = AltitudeUnit.METERS,
    val keepScreenOn: Boolean = true,
    val darkMode: Boolean = true,
    val gpsUpdateIntervalMs: Long = 1000L
)
