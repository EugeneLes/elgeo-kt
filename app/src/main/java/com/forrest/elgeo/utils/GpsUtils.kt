package com.forrest.elgeo.utils

import com.forrest.elgeo.domain.model.AltitudeUnit
import com.forrest.elgeo.domain.model.SpeedUnit
import java.text.DecimalFormat

object GpsUtils {

    private val speedFormat = DecimalFormat("#")
    private val distanceFormat = DecimalFormat("#.##")
    private val coordFormat = DecimalFormat("#.######")

    fun formatSpeed(metersPerSecond: Float, unit: SpeedUnit): String {
        val value = convertSpeed(metersPerSecond, unit)
        return speedFormat.format(value)
    }

    fun convertSpeed(metersPerSecond: Float, unit: SpeedUnit): Double = when (unit) {
        SpeedUnit.KMH -> metersPerSecond * 3.6
        SpeedUnit.MPH -> metersPerSecond * 2.23694
    }

    fun speedUnitLabel(unit: SpeedUnit): String = when (unit) {
        SpeedUnit.KMH -> "km/h"
        SpeedUnit.MPH -> "mph"
    }

    fun formatAltitude(meters: Double, unit: AltitudeUnit): String {
        val value = convertAltitude(meters, unit)
        return DecimalFormat("#").format(value)
    }

    fun convertAltitude(meters: Double, unit: AltitudeUnit): Double = when (unit) {
        AltitudeUnit.METERS -> meters
        AltitudeUnit.FEET -> meters * 3.28084
    }

    fun altitudeUnitLabel(unit: AltitudeUnit): String = when (unit) {
        AltitudeUnit.METERS -> "m"
        AltitudeUnit.FEET -> "ft"
    }

    fun formatDistance(meters: Double): String = if (meters >= 1000) {
        distanceFormat.format(meters / 1000) + " km"
    } else {
        distanceFormat.format(meters) + " m"
    }

    fun formatCoordinate(value: Double): String = coordFormat.format(value)

    fun formatDuration(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            "%d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }
}
