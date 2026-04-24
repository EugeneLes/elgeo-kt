package com.forrest.elgeo.domain.model

data class TripState(
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val tripId: Long? = null,
    val distance: Double = 0.0,
    val duration: Long = 0L,
    val maxSpeed: Float = 0f,
    val speedSum: Double = 0.0,
    val pointCount: Int = 0,
    val startTime: Long = 0L,
    val pauseTime: Long = 0L,
    val totalPauseDuration: Long = 0L
) {
    val avgSpeed: Float
        get() = if (pointCount > 0) (speedSum / pointCount).toFloat() else 0f
}
