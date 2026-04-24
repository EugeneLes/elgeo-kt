package com.forrest.elgeo.di

import android.content.Context
import androidx.room.Room
import com.forrest.elgeo.data.local.dao.LocationPointDao
import com.forrest.elgeo.data.local.dao.TripDao
import com.forrest.elgeo.data.local.database.ElGeoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ElGeoDatabase =
        Room.databaseBuilder(
            context,
            ElGeoDatabase::class.java,
            "elgeo_database"
        ).build()

    @Provides
    fun provideTripDao(database: ElGeoDatabase): TripDao = database.tripDao()

    @Provides
    fun provideLocationPointDao(database: ElGeoDatabase): LocationPointDao = database.locationPointDao()
}
