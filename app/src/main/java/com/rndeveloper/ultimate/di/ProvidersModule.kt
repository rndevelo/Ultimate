package com.rndeveloper.ultimate.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.rndeveloper.ultimate.receivers.GeofenceReceiver
import com.rndeveloper.ultimate.utils.Constants.REQUEST_CODE
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
    fun provideLocationProviderClient(@ApplicationContext appContext: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)


    //    Geocoder
    @Singleton
    @Provides
    fun provideGeocoder(@ApplicationContext appContext: Context) = Geocoder(appContext)


    //    GeofencingClient
    @Singleton
    @Provides
    fun provideGeofencingClient(@ApplicationContext appContext: Context): GeofencingClient =
        LocationServices.getGeofencingClient(appContext)


    //    PendingIntent
    @Singleton
    @Provides
    fun provideGeofencePendingIntent(
        @ApplicationContext appContext: Context,
    ): PendingIntent = PendingIntent.getBroadcast(
        appContext,
        REQUEST_CODE,
        Intent(appContext, GeofenceReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    //    Intent
    @Singleton
    @Provides
    fun provideGeofenceIntent(
        @ApplicationContext appContext: Context,
    ): Intent = Intent(appContext, GeofenceReceiver::class.java)


    //    Context
    @Singleton
    @Provides
    fun provideContext(@ApplicationContext appContext: Context): Context = appContext

}

