package com.rndeveloper.ultimate.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.usecases.login.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LoginUiState()
    )

    init {
        viewModelScope.launch {
            loginUseCases.checkUserLoggedInUseCase(Unit).collectLatest { newLoginUiState ->
                _state.update {
                    newLoginUiState
                }
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
            is LoginState.Register -> loginUseCases.registerUseCase(email to password)
        }.catch { error ->
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
    }

    fun recoverPassword(email: String) = viewModelScope.launch {
        loginUseCases.recoverPassUseCase(email).catch { error ->
            _state.update {
                it.copy(
                    errorMessage = CustomException.GenericException(
                        error.message ?: "Recover Pass Error"
                    )
                )
            }
        }.collectLatest { newLoginUIState ->
            _state.update {
                newLoginUIState
            }
        }
    }

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
}