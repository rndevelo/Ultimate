package com.rndeveloper.ultimate.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
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
class HistoryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserDataUseCase: GetUserDataUseCase,
) : ViewModel() {

    private val _userState = MutableStateFlow(UserUiState())
    val uiUserState: StateFlow<UserUiState> = _userState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserUiState()
    )

    private val _historyState = MutableStateFlow(HistoryUiState())
    val uiHistoryState: StateFlow<HistoryUiState> = _historyState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HistoryUiState()
    )

    init {
        viewModelScope.launch {
            awaitAll(
                async { onGetUserData() },
                async { onGetHistoryData() }
            )
        }
    }

    private fun onGetUserData() = viewModelScope.launch {
//        getUserDataUseCase(_userState.value.user.uid).collectLatest { newUserUiState ->
//            _userState.update {
//                newUserUiState
//            }
//        }
    }

    private fun onGetHistoryData() = viewModelScope.launch {
        userRepository.getHistoryData().collectLatest { result ->

            _historyState.update {

                result.fold(
                    onSuccess = { items ->
                        it.copy(history = items)
                    },
                    onFailure = { exception ->
                        it.copy(
                            isLoading = false,
                            errorMessage = CustomException.GenericException(
                                exception.message ?: "Exception, didn't can get data"
                            )
                        )
                    }
                )
            }
        }
    }
}

