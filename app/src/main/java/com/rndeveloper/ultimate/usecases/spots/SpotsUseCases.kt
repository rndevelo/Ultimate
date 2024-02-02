package com.rndeveloper.ultimate.usecases.spots

data class SpotsUseCases(
    val getSpotsUseCase: GetSpotsUseCase,
    val getAreasUseCase: GetAreasUseCase,
    val setSpotUseCase: SetSpotUseCase,
    val removeSpotUseCase: RemoveSpotUseCase,
)
