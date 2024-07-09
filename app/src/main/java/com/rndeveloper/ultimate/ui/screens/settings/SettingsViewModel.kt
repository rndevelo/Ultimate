package com.rndeveloper.ultimate.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
import com.rndeveloper.ultimate.usecases.login.LoginUseCases
import com.rndeveloper.ultimate.usecases.user.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val getUserDataUseCase: GetUserDataUseCase,
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val uiSettingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsUiState(),
    )

    init {
        viewModelScope.launch {
            awaitAll(
                async { onGetAuthState() },
                async { onGetUserData() },
            )
        }
    }

    private suspend fun onGetAuthState() {
        loginUseCases.checkUserLoggedInUseCase(Unit).collectLatest { newSettingsUiState ->
            _settingsState.update {
                it.copy(isLogged = newSettingsUiState.isLogged)
            }
        }
    }

    private suspend fun onGetUserData() {
        getUserDataUseCase(_settingsState.value.user.uid)
            .collectLatest { newUserUiState ->
                _settingsState.update {
                    it.copy(user = newUserUiState.user)
                }
            }
    }

    fun logOut() {
        loginRepository.logout()
    }

    fun deleteUser() = viewModelScope.launch {
        userRepository.deleteUserData().collectLatest { result ->
            if (result.getOrNull() == true) {
                loginRepository.deleteUser().collectLatest { isUserDeleted ->
                    isUserDeleted.onSuccess {
                        _settingsState.update {
                            it.copy(errorMessage = CustomException.GenericException("User deleted"))
                        }
                    }.onFailure { fail ->
                        _settingsState.update {
                            it.copy(errorMessage = CustomException.GenericException(fail.message!!))
                        }
                    }
                }
            }
        }
    }
}