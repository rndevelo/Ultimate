package com.rndeveloper.ultimate.di

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.repositories.LoginRepositoryImpl
import com.rndeveloper.ultimate.repositories.UserLocationRepository
import com.rndeveloper.ultimate.repositories.UserLocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository =
        LoginRepositoryImpl(firebaseAuth)

    @Singleton
    @Provides
    fun provideLocationRepository(fusedLocationProviderClient: FusedLocationProviderClient): UserLocationRepository =
        UserLocationRepositoryImpl(fusedLocationProviderClient)

}
