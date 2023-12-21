package com.rndeveloper.ultimate.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rndeveloper.ultimate.repositories.ActivityTransitionRepo
import com.rndeveloper.ultimate.repositories.ActivityTransitionRepoImpl
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.GeocoderRepositoryImpl
import com.rndeveloper.ultimate.repositories.GeofenceClient
import com.rndeveloper.ultimate.repositories.GeofenceClientImpl
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.LocationClientImpl
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.repositories.LoginRepositoryImpl
import com.rndeveloper.ultimate.repositories.SpotRepository
import com.rndeveloper.ultimate.repositories.SpotRepositoryImpl
import com.rndeveloper.ultimate.repositories.TimerRepository
import com.rndeveloper.ultimate.repositories.TimerRepositoryImpl
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.repositories.UserRepositoryImpl
import com.rndeveloper.ultimate.usecases.login.CheckUserLoggedInUseCase
import com.rndeveloper.ultimate.usecases.login.LoginEmailPassUseCase
import com.rndeveloper.ultimate.usecases.login.LoginUseCases
import com.rndeveloper.ultimate.usecases.login.LoginWithGoogleUseCase
import com.rndeveloper.ultimate.usecases.login.RecoverPassUseCase
import com.rndeveloper.ultimate.usecases.login.RegisterUseCase
import com.rndeveloper.ultimate.usecases.spots.GetSpotsUseCase
import com.rndeveloper.ultimate.usecases.spots.RemoveSpotUseCase
import com.rndeveloper.ultimate.usecases.spots.SetSpotUseCase
import com.rndeveloper.ultimate.usecases.spots.SpotsUseCases
import com.rndeveloper.ultimate.usecases.user.GetUserDataUseCase
import com.rndeveloper.ultimate.usecases.user.UserUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository =
        LoginRepositoryImpl(firebaseAuth = firebaseAuth)

    @Singleton
    @Provides
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore
    ): UserRepository =
        UserRepositoryImpl(firebaseAuth = firebaseAuth, fireStore = fireStore)

    @Singleton
    @Provides
    fun provideLocationRepository(fusedLocationProviderClient: FusedLocationProviderClient): LocationClient =
        LocationClientImpl(fusedLocationProviderClient = fusedLocationProviderClient)


    @Singleton
    @Provides
    fun provideTimerRepository(userPreferencesRepository: DataStore<Preferences>): TimerRepository =
        TimerRepositoryImpl(userPreferencesRepository = userPreferencesRepository)

    @Singleton
    @Provides
    fun provideSpotsRepository(
        fireStore: FirebaseFirestore
    ): SpotRepository = SpotRepositoryImpl(fireStore = fireStore)

    @Singleton
    @Provides
    fun provideActivityRecognitionRepository(
        @ApplicationContext appContext: Context,
        activityRecognitionClient: ActivityRecognitionClient,
    ): ActivityTransitionRepo = ActivityTransitionRepoImpl(
        activityRecognitionClient = activityRecognitionClient,
        appContext = appContext
    )


    @Singleton
    @Provides
    fun provideGeocoderRepository(geocoder: Geocoder): GeocoderRepository =
        GeocoderRepositoryImpl(geocoder = geocoder)

    @Singleton
    @Provides
    fun provideGeofenceClient(
        @ApplicationContext appContext: Context,
        geofencingClient: GeofencingClient,
        geofenceIntent: Intent,
        geofencePendingIntent: PendingIntent
    ): GeofenceClient = GeofenceClientImpl(
        appContext = appContext,
        geofencingClient = geofencingClient,
    )


    //    USE CASES
    @Provides
    fun provideLoginUseCases(
        repo: LoginRepository
    ) = LoginUseCases(
        checkUserLoggedInUseCase = CheckUserLoggedInUseCase(repo),
        loginEmailPassUseCase = LoginEmailPassUseCase(repo),
        loginWithGoogleUseCase = LoginWithGoogleUseCase(repo),
        registerUseCase = RegisterUseCase(repo),
        recoverPassUseCase = RecoverPassUseCase(repo),
    )

    @Provides
    fun provideUserUseCases(
        repo: UserRepository
    ) = UserUseCases(
        getUserDataUseCase = GetUserDataUseCase(repo),
    )

    @Provides
    fun provideSpotUseCases(
        repo: SpotRepository
    ) = SpotsUseCases(
        getSpotsUseCase = GetSpotsUseCase(repo),
        setSpotUseCase = SetSpotUseCase(repo),
        removeSpotUseCase = RemoveSpotUseCase(repo)
    )

}
