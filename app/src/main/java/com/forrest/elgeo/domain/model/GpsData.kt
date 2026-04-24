package com.forrest.elgeo.domain.model

data class GpsData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val speed: Float = 0f,
    val accuracy: Float = 0f,
    val bearing: Float = 0f,
    val timestamp: Long = 0L,
    val altitudeTrend: AltitudeTrend = AltitudeTrend.STABLE,
    val hasFix: Boolean = false
)

enum class AltitudeTrend { RISING, FALLING, STABLE }
enum class SpeedUnit { KMH, MPH }
enum class AltitudeUnit { METERS, FEET }
