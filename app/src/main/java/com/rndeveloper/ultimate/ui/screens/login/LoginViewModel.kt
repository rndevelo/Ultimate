package com.rndeveloper.ultimate.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginState
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginUiState
import com.rndeveloper.ultimate.ui.screens.login.uistates.RecoverPassUiState
import com.rndeveloper.ultimate.usecases.login.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val credentialManager: CredentialManager,
    private val credentialRequest: GetCredentialRequest,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LoginUiState()
    )

    private val _recoverPassUIState = MutableStateFlow(RecoverPassUiState())
    val recoverPassUIState = _recoverPassUIState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecoverPassUiState(),
    )

    init {
        viewModelScope.launch {
            awaitAll(
                async { checkUserLogged() },
                async { verifyEmail() },
            )
        }
    }

    private suspend fun checkUserLogged() {
        loginUseCases.checkUserLoggedInUseCase(Unit).collectLatest { newLoginUiState ->
            _state.update {
                it.copy(isLogged = newLoginUiState.isLogged)
            }
        }
    }

    private suspend fun verifyEmail() {
        loginUseCases.verifyEmailIsVerifiedUseCase(Unit).collectLatest { newLoginUiState ->
            if (newLoginUiState.isEmailVerified) {
                _state.update {
                    it.copy(isEmailSent = false, isEmailVerified = true)
                }
                checkUserLogged()
            }
        }
    }

    fun changeScreenState() = viewModelScope.launch {
        _state.update {
            when (it.screenState) {
                is LoginState.Login -> it.copy(screenState = LoginState.Register())
                is LoginState.Register -> it.copy(screenState = LoginState.Login())
            }
        }
    }

    fun signInOrSignUp(email: String, password: String) = viewModelScope.launch {
        when (_state.value.screenState) {
            is LoginState.Login -> loginUseCases.loginEmailPassUseCase(email to password)
                .catch { error ->
                    _state.update { loginState ->
                        loginState.copy(
                            errorMessage = CustomException.GenericException(
                                error.message ?: "Login Error"
                            )
                        )
                    }
                }.collectLatest { newLoginUIState ->
                    _state.update {
                        newLoginUIState
                    }
                }

            is LoginState.Register -> loginUseCases.registerUseCase(email to password)
                .collectLatest { newLoginUIState ->

                    if (newLoginUIState.isRegistered) {
                        loginUseCases.sendEmailVerificationUseCase(Unit)
                            .collectLatest { newEmailSentLoginUIState ->
                                _state.update {
                                    newEmailSentLoginUIState
                                }
                            }
                    }
                }
        }
    }

    fun updateRecoveryPasswordState(newRecoverPassUiState: RecoverPassUiState) {
        _recoverPassUIState.update { newRecoverPassUiState }
    }

    fun recoverPassword(email: String) = viewModelScope.launch {
        loginUseCases.recoverPassUseCase(email).catch { error ->
            _recoverPassUIState.update {
                it.copy(
                    errorMessage = CustomException.GenericException(
                        error.message ?: "Recover Pass Error"
                    )
                )
            }
        }.collectLatest { newLoginUIState ->
            _recoverPassUIState.update {
                newLoginUIState
            }
        }
    }

    //    Google sing in
    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) = viewModelScope.launch {
        val account = task.result as GoogleSignInAccount
        loginUseCases.loginWithGoogleUseCase(account.idToken!!).let { result ->
            result.collectLatest { newLoginUIState ->
                _state.update {
                    newLoginUIState
                }
            }
        }
    }

    fun handleSignIn(context: Context)= viewModelScope.launch {
        // Handle the successfully returned credential.

        val credentialResponse = credentialManager.getCredential(
            request = credentialRequest,
            context = context,
        )

        val credential = credentialResponse.credential


        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Use googleIdTokenCredential and extract id to validate and
                // authenticate on your server.
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)

                loginUseCases.loginWithGoogleUseCase(googleIdTokenCredential.idToken).let { result ->
                    result.collectLatest { newLoginUIState ->
                        _state.update {
                            newLoginUIState
                        }
                    }
                }
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("TAG", "Received an invalid google id token response", e)
            }
        } else {
            // Catch any unrecognized custom credential type here.
            Log.e("TAG", "Unexpected type of credential")
        }
    }
}