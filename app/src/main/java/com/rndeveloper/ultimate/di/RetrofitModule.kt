package com.rndeveloper.ultimate.di

import com.rndeveloper.ultimate.annotations.FirebaseService
import com.rndeveloper.ultimate.annotations.OpenRouteService
import com.rndeveloper.ultimate.backend.ApiService
import com.rndeveloper.ultimate.notifications.NotificationAPI
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@dagger.Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    @OpenRouteService
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideApiService(@OpenRouteService retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    @Singleton
    @Provides
    @FirebaseService
    fun providerRetrofit2(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Singleton
    @Provides
    fun provideNotificationAPI(@FirebaseService retrofit: Retrofit): NotificationAPI =
        retrofit.create(NotificationAPI::class.java)
}