package com.rndeveloper.ultimate.ui.screens.home

import android.location.Geocoder
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.extensions.getAddressList
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.model.SpotType
import com.rndeveloper.ultimate.model.Timer
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.TimerRepository
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.usecases.spots.SpotsUseCases
import com.rndeveloper.ultimate.usecases.user.UserUseCases
import com.rndeveloper.ultimate.utils.Constants
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME
import com.rndeveloper.ultimate.utils.Constants.INTERVAL
import com.rndeveloper.ultimate.utils.Utils.currentTime
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
class HomeViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val locationClient: LocationClient,
    private val spotsUseCases: SpotsUseCases,
    private val userUseCases: UserUseCases,
    private val userRepository: UserRepository,
//    private val geofenceClient: GeofenceClient,
    private val geocoder: Geocoder,
) : ViewModel() {

    private val _locationState = MutableStateFlow(LocationUiState())
    val uiLocationState: StateFlow<LocationUiState> = _locationState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LocationUiState()
    )

    private val _userState = MutableStateFlow(UserUiState())
    val uiUserState: StateFlow<UserUiState> = _userState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserUiState()
    )

    private val _spotsState = MutableStateFlow(SpotsUiState())
    val uiSpotsState: StateFlow<SpotsUiState> = _spotsState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SpotsUiState()
    )

    private val _elapsedTimeState = MutableStateFlow(0L)
    val uiElapsedTimeState: StateFlow<Long> = _elapsedTimeState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0L,
    )

    private val _directionsState = MutableStateFlow(DirectionsUiState())
    val uiDirectionsState: StateFlow<DirectionsUiState> = _directionsState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DirectionsUiState(),
    )

    init {
        onGetAndStartTimer()
//        TODO: Refactor this
        viewModelScope.launch {
            awaitAll(
                async { onGetLocationData() },
                async { onGetUserData() },
            )
        }
    }

    //    TIMER
    private fun onGetAndStartTimer() = viewModelScope.launch {
//        _spotsState.value.spots.firstOrNull()?.let { onSpotSelected(it.tag) }
        timerRepository.getTimer(Constants.SPOTS_TIMER).collectLatest { timer ->

            if (timer.endTime > currentTime()) {
                object : CountDownTimer(
                    timer.endTime - currentTime(),
                    INTERVAL
                ) {
                    override fun onTick(millisUntilFinished: Long) {
                        _elapsedTimeState.update {
                            millisUntilFinished
                        }
                    }

                    override fun onFinish() {
                        _elapsedTimeState.update {
                            DEFAULT_ELAPSED_TIME
                        }
                    }
                }.start()
            }
        }
    }

    fun onSaveGetStartTimer(timerId: String) {
        val timer = Timer(
            id = timerId,
            endTime = currentTime() + Constants.TIMER
        )
        if (_elapsedTimeState.value <= 0L && _spotsState.value.spots.isNotEmpty()) {
            viewModelScope.launch {
                timerRepository.saveTimer(timer = timer)
            }
            onGetAndStartTimer()
        }
    }

    private suspend fun onGetUserData() {
        userUseCases.getUserDataUseCase(Unit).collectLatest { newUserUiState ->
            _userState.update {
                newUserUiState
            }
        }
    }

    private suspend fun onGetLocationData() {
//        _locationState.update {
//            it.copy(isLoading = true)
//        }
        locationClient.getLocationsRequest().collectLatest { newLocation ->
            _locationState.update {
                it.copy(location = newLocation)
            }
        }
    }

    fun onGetAddressLine(position: Position) {
        _spotsState.update {
            it.copy(isLoading = true)
        }
        LatLng(position.lat, position.lng).getAddressList(geocoder) { address ->

            Directions(
                addressLine = address.first().getAddressLine(0),
                locality = address.first().locality,
                area = address.first().subAdminArea,
                country = address.first().countryName
            ).let { directions ->
                _directionsState.update {
                    it.copy(directions = directions)
                }
                onGetSpots(position)
            }
        }
    }


    //    GET SPOTS
    fun onGetSpots(
        position: Position,
        directions: Directions = _directionsState.value.directions
    ) = viewModelScope.launch {

        _locationState.value.let { locPosition ->
            spotsUseCases.getSpotsUseCase(
                Triple(
                    position,
                    locPosition.location!!,
                    directions
                )
            ).collectLatest { newSpotsUiState ->
                _spotsState.update {
                    newSpotsUiState
                }
            }
        }
    }


    //    SET SPOT
    fun onSet(position: Position, screenState: ScreenState) = viewModelScope.launch {
        when (screenState) {
            ScreenState.ADDSPOT -> {
                Spot().copy(
                    timestamp = currentTime(),
                    type = SpotType.BLUE,
                    directions = _directionsState.value.directions,
                    position = position,
                    user = _userState.value.user
                ).let { spot ->
                    spotsUseCases.setSpotUseCase(spot)
                }
                    .collectLatest { newHomeUiState ->
                        _spotsState.update {
                            it
                        }
                    }
            }

            ScreenState.PARKMYCAR -> {
                _userState.value.user.copy(car = position).let { user ->
                    userRepository.setUserCar(user)
                }
                    .collectLatest { newHomeUiState ->
                        _userState.update {
                            it
                        }
                    }
            }

            else -> {}
        }
    }

    fun onRemoveSpot(spot: Spot) = viewModelScope.launch {
        spotsUseCases.removeSpotUseCase(spot).collectLatest { newHomeUiState ->
            _spotsState.update {
                newHomeUiState
            }
        }
    }

//    fun onSetMyCar(position: Position) = viewModelScope.launch {
//        _userState.value.user.copy(car = position).let { user ->
//            userRepository.setUserCar(user).collectLatest { newUserUiState ->
//                _userState.update {
//                    //                    it.copy(isLoading = newHomeUiState.isSuccess)
//                    it
//                }
//            }
//        }
//    }


    //    SPOT SELECTED
    fun onSpotSelected(spotTag: String) {

        val spot = _spotsState.value.spots.find {
            it.tag == spotTag
        } ?: _spotsState.value.spots.firstOrNull()

        _spotsState.update {
            it.copy(selectedSpot = spot)
        }
    }

}
