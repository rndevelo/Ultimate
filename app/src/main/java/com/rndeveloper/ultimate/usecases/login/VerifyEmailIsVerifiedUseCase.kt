package com.rndeveloper.ultimate.usecases.login

import com.rndeveloper.ultimate.repositories.LoginRepositoryImpl
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginSignState
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class VerifyEmailIsVerifiedUseCase @Inject constructor(
    private val repository: LoginRepositoryImpl,
) : BaseUseCase<Unit, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Unit): Flow<LoginUiState> =
        channelFlow {

            repository.verifiedAccount.collectLatest {
                send(
                    LoginUiState().copy(
                        loginSignState = LoginSignState.SignIn(),
                        isEmailVerified = it,
                        isLoading = false
                    )
                )
            }

        }
}
