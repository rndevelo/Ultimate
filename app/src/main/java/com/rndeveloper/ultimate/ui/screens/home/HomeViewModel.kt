package com.rndeveloper.ultimate.ui.screens.home

import android.location.Geocoder
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.extensions.getAddressList
import com.rndeveloper.ultimate.model.Car
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
    private val geocoder: Geocoder,
) : ViewModel() {

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

    private val _addressLineState = MutableStateFlow("Sin coincidencias")
    val uiAddressLineState: StateFlow<String> = _addressLineState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "Sin coincidencias",
    )

    init {

//        TODO: Refactor this
        viewModelScope.launch {
            awaitAll(
                async { onGetAndStartTimer() },
                async { onGetUserData() },
                async { onGetLocationData() },
            )
        }
    }

    //    TIMER
    private fun onGetAndStartTimer() = viewModelScope.launch {
        _spotsState.value.spots.firstOrNull()?.let { onSpotSelected(it.tag) }
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
                            0L
                        }
                    }
                }.start()
            }
        }
    }

    suspend fun onSaveGetStartTimer(timerId: String) {
        val timer = Timer(
            id = timerId,
            endTime = currentTime() + Constants.TIMER
        )
        if (_elapsedTimeState.value <= 0L && _spotsState.value.spots.isNotEmpty()) {
            timerRepository.saveTimer(timer = timer)
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
        _userState.update {
            it.copy(isLoading = true)
        }
        locationClient.getLocationsRequest().collectLatest { userLocation ->
            _userState.update {
                it.copy(
//                    isLoading = false,
                    user = it.user.copy(loc = Position(userLocation.lat, userLocation.lng))
                )
            }
        }
    }


    //    GET SPOTS
    fun onGetSpots(camPosition: Position) = viewModelScope.launch {
        _userState.value.user.loc?.let { locPosition ->
            spotsUseCases.getSpotsUseCase(camPosition to locPosition)
                .collectLatest { newSpotsUiState ->
                    _spotsState.update {
                        newSpotsUiState
                    }
                }
        }
    }


    //    SET SPOT
    fun onSetSpot(position: Position) = viewModelScope.launch {

        val spot = Spot().copy(
            timestamp = currentTime(),
            type = SpotType.BLUE,
            position = position,
            user = _userState.value.user
        )

        spotsUseCases.setSpotUseCase(spot).collectLatest { newHomeUiState ->
            _spotsState.update {
                it
            }
        }
    }

    fun onRemoveSpot(spot: Spot) = viewModelScope.launch {
        spotsUseCases.removeSpotUseCase(spot).collectLatest { newHomeUiState ->
            _spotsState.update {
                newHomeUiState
            }
        }
    }

    fun onSetMyCar(car: Car) = viewModelScope.launch {
        _userState.value.user.copy(car = car).let { user ->
            userRepository.setUserCar(user).collectLatest { newUserUiState ->
                _userState.update {
                    //                    it.copy(isLoading = newHomeUiState.isSuccess)
                    it
                }
            }
        }
    }


    //    SPOT SELECTED
    fun onSpotSelected(spotTag: String) {

        val spot = _spotsState.value.spots.find {
            it.tag == spotTag
        } ?: _spotsState.value.spots.firstOrNull()

        _spotsState.update {
            it.copy(selectedSpot = spot)
        }
    }

    fun onGetAddressLine(latLng: LatLng) {
        latLng.getAddressList(geocoder) { address ->
            _addressLineState.update {
                address.first().locality + ", " + address.first().subAdminArea
            }
        }
    }
}
