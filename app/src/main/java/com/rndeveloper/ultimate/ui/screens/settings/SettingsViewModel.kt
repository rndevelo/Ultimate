package com.rndeveloper.ultimate.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.usecases.login.LoginUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val uiSettingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsUiState(),
    )

    init {

        viewModelScope.launch {
            loginUseCases.checkUserLoggedInUseCase(Unit).collectLatest { newSettingsUiState ->
                _settingsState.update {
                    it.copy(isLogged = newSettingsUiState.isLogged)
                }
            }
        }

        viewModelScope.launch {
//            _state.update { st ->
//                st.copy(
//                    userData = User(
//                        email = firebaseAuth.currentUser?.email.toString(),
//                        name = firebaseAuth.currentUser?.displayName.toString(),
//                        pass = "",
//                        photo = firebaseAuth.currentUser?.photoUrl.toString(),
//                    ),
//                )
//            }
        }
    }

    fun logOut() {
        loginRepository.logout()
    }
    fun deleteUser() = viewModelScope.launch {
        loginRepository.deleteUser().collectLatest {  }
    }
}