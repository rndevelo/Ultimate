package com.rndeveloper.ultimate.di

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    //    Location
    @Singleton
    @Provides
    fun provideLocationProviderClient(@ApplicationContext appContext: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

}
