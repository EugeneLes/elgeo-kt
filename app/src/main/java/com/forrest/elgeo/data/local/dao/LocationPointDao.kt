package com.forrest.elgeo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.forrest.elgeo.data.local.entity.LocationPointEntity

@Dao
interface LocationPointDao {
    @Insert
    suspend fun insert(point: LocationPointEntity)

    @Insert
    suspend fun insertAll(points: List<LocationPointEntity>)

    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getPointsForTrip(tripId: Long): List<LocationPointEntity>

    @Query("SELECT COUNT(*) FROM location_points WHERE tripId = :tripId")
    suspend fun getPointCount(tripId: Long): Int

    @Query("DELETE FROM location_points WHERE tripId = :tripId")
    suspend fun deletePointsForTrip(tripId: Long)
}
