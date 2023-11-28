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

    private val _uiHomeState = MutableStateFlow(HomeUiState())
    val uiHomeState: StateFlow<HomeUiState> = _uiHomeState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState()
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
        uiHomeState.value.spots.firstOrNull()?.let { onSpotSelected(it.tag) }
        timerRepository.getTimer(Constants.SPOTS_TIMER).collectLatest { timer ->
            if (timer.endTime > currentTime()) {
                object : CountDownTimer(
                    timer.endTime - currentTime(),
                    INTERVAL
                ) {
                    override fun onTick(millisUntilFinished: Long) {
                        _uiHomeState.update {
                            it.copy(elapsedTime = millisUntilFinished)
                        }
                    }

                    override fun onFinish() {
                        _uiHomeState.update {
                            it.copy(elapsedTime = 0L)
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
        if (uiHomeState.value.elapsedTime <= 0L && uiHomeState.value.spots.isNotEmpty()) {
            timerRepository.saveTimer(timer = timer)
            onGetAndStartTimer()
        }
    }

    private suspend fun onGetUserData() {
        userUseCases.getUserDataUseCase(Unit).collectLatest { newHomeUiState ->
            _uiHomeState.update {
                newHomeUiState
            }
        }
    }

    private suspend fun onGetLocationData() {
        _uiHomeState.update {
            it.copy(isLoading = true)
        }
        locationClient.getLocationsRequest().collectLatest { userLocation ->
            _uiHomeState.update {
                it.copy(isLoading = false, loc = userLocation)
            }
        }
    }


    //    GET SPOTS
    fun onGetSpots(camLatLng: LatLng) = viewModelScope.launch {
        spotsUseCases.getSpotsUseCase(camLatLng to _uiHomeState.value)
            .collectLatest { newHomeUiState ->
                _uiHomeState.update {
                    newHomeUiState
                }
            }
    }


    //    SET SPOT
    fun onSetSpot(targetLatLng: LatLng) = viewModelScope.launch {

        val spot = uiHomeState.value.user?.let {
            Spot().copy(
                timestamp = currentTime(),
                type = SpotType.BLUE,
                position = Position(targetLatLng.latitude, targetLatLng.longitude),
                user = it
            )
        }

        if (spot != null) {
            spotsUseCases.setSpotUseCase(spot).collectLatest { newHomeUiState ->
                _uiHomeState.update {
                    newHomeUiState.copy(spots = uiHomeState.value.spots)
                }
            }
        }
    }

    fun onRemoveSpot(spot: Spot) = viewModelScope.launch {
        spotsUseCases.removeSpotUseCase(spot).collectLatest { newHomeUiState ->
            _uiHomeState.update {
                newHomeUiState
            }
        }
    }

    fun onSetMyCar(latLng: LatLng) = viewModelScope.launch {

        val car = Car(lat = latLng.latitude, lng = latLng.longitude)
        _uiHomeState.value.user?.copy(car = car)?.let { user ->
            userRepository.setUserCar(user).collectLatest { newHomeUiState ->
                _uiHomeState.update {
                    it
                }
            }
        }
    }


    //    SPOT SELECTED
    fun onSpotSelected(spotTag: String) {

        val spot = uiHomeState.value.spots.find {
            it.tag == spotTag
        } ?: uiHomeState.value.spots.firstOrNull()

        _uiHomeState.update {
            it.copy(selectedSpot = spot)
        }
    }

    fun onGetAddressLine(latLng: LatLng) {
        latLng.getAddressList(geocoder) { address ->
            _uiHomeState.update {
                it.copy(
                    addressLine = address.first().getAddressLine(0)
                        .ifEmpty { "Ubicación desconocida" },
                )
            }
        }
    }
}
