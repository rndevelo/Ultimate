package com.rndeveloper.ultimate.di

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvidersModule {


    //    Location
    @Singleton
    @Provides
    fun provideLocationClient(@ApplicationContext appContext: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

//    Activity Recognition
    @Singleton
    @Provides
    fun provideActivityRecognitionClient(@ApplicationContext appContext: Context): ActivityRecognitionClient =
    ActivityRecognition.getClient(appContext)


    //    Geocoder
    @Singleton
    @Provides
    fun provideGeocoder(@ApplicationContext appContext: Context) = Geocoder(appContext)


    //    GeofencingClient
    @Singleton
    @Provides
    fun provideGeofencingClient(@ApplicationContext appContext: Context): GeofencingClient =
        LocationServices.getGeofencingClient(appContext)


    //    Context
    @Singleton
    @Provides
    fun provideContext(@ApplicationContext appContext: Context): Context = appContext

}

