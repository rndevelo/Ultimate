package com.rndeveloper.ultimate.usecases.login

data class LoginUseCases(
    val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    val loginEmailPassUseCase: LoginEmailPassUseCase,
    val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    val registerUseCase: RegisterUseCase,
    val recoverPassUseCase: RecoverPassUseCase,
)
