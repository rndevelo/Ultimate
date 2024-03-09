package com.rndeveloper.ultimate.di

import com.rndeveloper.ultimate.backend.ApiService
import com.rndeveloper.ultimate.notifications.NotificationAPI
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@dagger.Module
@InstallIn(SingletonComponent::class)
object RetrofitNotification {

//    @Singleton
//    @Provides
//    fun provideRetrofitMessaging(): Retrofit =
//        Retrofit.Builder()
//            .baseUrl("https://fcm.googleapis.com")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//
//    @Singleton
//    @Provides
//    fun provideNotificationAPI(retrofit: Retrofit): NotificationAPI =
//        retrofit.create(NotificationAPI::class.java)

}