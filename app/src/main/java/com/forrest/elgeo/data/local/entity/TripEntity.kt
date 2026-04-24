package com.forrest.elgeo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startTime: Long,
    val endTime: Long? = null,
    val distance: Double = 0.0,
    val maxSpeed: Float = 0f,
    val avgSpeed: Float = 0f,
    val status: String = "active"
)
