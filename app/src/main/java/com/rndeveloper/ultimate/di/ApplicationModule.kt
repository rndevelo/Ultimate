package com.rndeveloper.ultimate.di

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rndeveloper.ultimate.backend.notifications.NotificationAPI
import com.rndeveloper.ultimate.repositories.ActivityTransitionRepo
import com.rndeveloper.ultimate.repositories.ActivityTransitionRepoImpl
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.GeocoderRepositoryImpl
import com.rndeveloper.ultimate.repositories.GeofenceClient
import com.rndeveloper.ultimate.repositories.GeofenceClientImpl
import com.rndeveloper.ultimate.repositories.HelpRepository
import com.rndeveloper.ultimate.repositories.HelpRepositoryImpl
import com.rndeveloper.ultimate.repositories.ItemsRepository
import com.rndeveloper.ultimate.repositories.ItemsRepositoryImpl
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.LocationClientImpl
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.repositories.LoginRepositoryImpl
import com.rndeveloper.ultimate.repositories.NetworkConnectivity
import com.rndeveloper.ultimate.repositories.NetworkConnectivityImpl
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
import com.rndeveloper.ultimate.usecases.login.SendEmailVerificationUseCase
import com.rndeveloper.ultimate.usecases.login.VerifyEmailIsVerifiedUseCase
import com.rndeveloper.ultimate.usecases.spots.GetAreasUseCase
import com.rndeveloper.ultimate.usecases.spots.GetSpotsUseCase
import com.rndeveloper.ultimate.usecases.spots.RemoveSpotUseCase
import com.rndeveloper.ultimate.usecases.spots.SetSpotUseCase
import com.rndeveloper.ultimate.usecases.spots.SpotsUseCases
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
    fun provideLocationRepository(fusedLocationProviderClient: FusedLocationProviderClient): LocationClient =
        LocationClientImpl(fusedLocationProviderClient = fusedLocationProviderClient)

    @Singleton
    @Provides
    fun provideHelpRepository(userPreferencesRepository: DataStore<Preferences>): HelpRepository =
        HelpRepositoryImpl(userPreferencesRepository = userPreferencesRepository)

    @Singleton
    @Provides
    fun provideTimerRepository(userPreferencesRepository: DataStore<Preferences>): TimerRepository =
        TimerRepositoryImpl(userPreferencesRepository = userPreferencesRepository)

    @Provides
    fun provideNetworkConnectivity(connectivityManager: ConnectivityManager): NetworkConnectivity =
        NetworkConnectivityImpl(connectivityManager = connectivityManager)

    @Singleton
    @Provides
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth,
    ): LoginRepository = LoginRepositoryImpl(firebaseAuth = firebaseAuth)

    @Singleton
    @Provides
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore
    ): UserRepository =
        UserRepositoryImpl(firebaseAuth = firebaseAuth, fireStore = fireStore)

    @Singleton
    @Provides
    fun provideSpotsRepository(
        fireAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        userRepository: UserRepository,
        notificationAPI: NotificationAPI,
    ): ItemsRepository = ItemsRepositoryImpl(
        fireAuth = fireAuth,
        fireStore = fireStore,
        userRepository = userRepository,
        notificationAPI = notificationAPI
    )

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
        geofencingClient: GeofencingClient
    ): GeofenceClient = GeofenceClientImpl(
        appContext = appContext,
        geofencingClient = geofencingClient,
    )

    //    USE CASES
    @Provides
    fun provideLoginUseCases(
        repo: LoginRepository,
        repoImpl: LoginRepositoryImpl,
    ) = LoginUseCases(
        checkUserLoggedInUseCase = CheckUserLoggedInUseCase(repo),
        loginEmailPassUseCase = LoginEmailPassUseCase(repo),
        loginWithGoogleUseCase = LoginWithGoogleUseCase(repo),
        registerUseCase = RegisterUseCase(repo),
        recoverPassUseCase = RecoverPassUseCase(repo),
        sendEmailVerificationUseCase = SendEmailVerificationUseCase(repo),
        verifyEmailIsVerifiedUseCase = VerifyEmailIsVerifiedUseCase(repoImpl),
    )

    @Provides
    fun provideSpotUseCases(
        repo: ItemsRepository
    ) = SpotsUseCases(
        getSpotsUseCase = GetSpotsUseCase(repo),
        getAreasUseCase = GetAreasUseCase(repo),
        setSpotUseCase = SetSpotUseCase(repo),
        removeSpotUseCase = RemoveSpotUseCase(repo)
    )
}
