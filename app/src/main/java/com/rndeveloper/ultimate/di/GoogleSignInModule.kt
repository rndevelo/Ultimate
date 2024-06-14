package com.rndeveloper.ultimate.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.rndeveloper.ultimate.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleSignInModule {


    @Singleton
    @Provides
    fun signInWithGoogleOption(): GetSignInWithGoogleOption =
        GetSignInWithGoogleOption.Builder(BuildConfig.WEB_ID_CLIENT)
            .build()


    //    1
    @Singleton
    @Provides
    fun credentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    //    2
    @Singleton
    @Provides
    fun credentialRequest(
        signInWithGoogleOption: GetSignInWithGoogleOption
    ): GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

}
