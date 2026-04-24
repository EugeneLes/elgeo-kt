package com.forrest.elgeo.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.forrest.elgeo.data.local.dao.LocationPointDao
import com.forrest.elgeo.data.local.dao.TripDao
import com.forrest.elgeo.data.local.entity.LocationPointEntity
import com.forrest.elgeo.data.local.entity.TripEntity

@Database(
    entities = [TripEntity::class, LocationPointEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ElGeoDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun locationPointDao(): LocationPointDao
}
