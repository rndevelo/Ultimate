package com.rndeveloper.ultimate.ui.screens.home

import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.model.SpotType
import com.rndeveloper.ultimate.model.Timer
import com.rndeveloper.ultimate.repositories.ActivityTransitionRepo
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.ItemsRepository
import com.rndeveloper.ultimate.repositories.TimerRepository
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.LocationUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
import com.rndeveloper.ultimate.usecases.spots.SpotsUseCases
import com.rndeveloper.ultimate.usecases.user.UserUseCases
import com.rndeveloper.ultimate.utils.Constants
import com.rndeveloper.ultimate.utils.Constants.AREA_COLLECTION_REFERENCE
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME
import com.rndeveloper.ultimate.utils.Constants.INTERVAL
import com.rndeveloper.ultimate.utils.Constants.SPOT_COLLECTION_REFERENCE
import com.rndeveloper.ultimate.utils.Utils.currentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val locationClient: LocationClient,
    private val spotsUseCases: SpotsUseCases,
    private val spotsRepository: ItemsRepository,
    private val userUseCases: UserUseCases,
    private val userRepository: UserRepository,
    private val activityTransitionClient: ActivityTransitionRepo,
    private val geocoderRepository: GeocoderRepository,
//    private val geofenceClient: GeofenceClient,
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

    private val _spotState = MutableStateFlow(Spot())
    val uiSpotState: StateFlow<Spot?> = _spotState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private val _areasState = MutableStateFlow(AreasUiState())
    val uiAreasState: StateFlow<AreasUiState> = _areasState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AreasUiState()
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
                async { onGetUserData() },
                async { onGetLocationData() },
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
        if (_elapsedTimeState.value <= 0L && _spotsState.value.spots.isNotEmpty() && _userState.value.user.points >= 2) {
            viewModelScope.launch {
                timerRepository.saveTimer(timer = timer)
            }
            onSetPoint(points = -2)
            onGetAndStartTimer()
        }
    }

    private suspend fun onGetUserData() {
        userUseCases.getUserDataUseCase(Unit).collectLatest { newUserUiState ->
            _userState.update {
                newUserUiState
            }
            activityTransitionClient.startActivityTransition(user = newUserUiState.user)
        }
    }

    private suspend fun onGetLocationData() {
        _locationState.update {
            it.copy(isLoading = true)
        }


//        var loc: Position? = null

        locationClient.getLocationsRequest().collectLatest { newLocation ->
//            loc = newLocation

//            _spotsState.update {
//                it.copy(spots = _spotsState.value.spots.map { spot ->
//                    val distance = FloatArray(2)
//                    _locationState.value.location?.let { it1 ->
//                        Location.distanceBetween(
//                            spot.position.lat, spot.position.lng,
//                            it1.lat, it1.lng, distance
//                        )
//                    }
//                    spot.copy(distance = "${distance[0].toInt()}m")
//                })
//            }
            _locationState.update {
                it.copy(location = newLocation, isLoading = false)
            }
        }
    }

    fun onGetAddressLine(
        context: Context,
        camPosState: CameraPositionState,
        screenState: ScreenState,
        doLoad: Boolean,
    ) {

        if (screenState == ScreenState.MAIN) {
            _spotsState.update {
                it.copy(isLoading = true)
            }
        }

        if (doLoad) {

            val currentPosition = Position(
                camPosState.position.target.latitude,
                camPosState.position.target.longitude
            )

            geocoderRepository.getAddressList(
                LatLng(
                    currentPosition.lat,
                    currentPosition.lng
                )
            ) { directions ->
                if (screenState == ScreenState.MAIN) {
                    onGetSpots(
                        context = context,
                        position = currentPosition,
                        directions = directions
                    )
                }
                _directionsState.update {
                    it.copy(directions = directions)
                }
            }
        }
    }


    //    GET SPOTS
    private fun onGetSpots(
        context: Context,
        position: Position,
        directions: Directions
    ) = viewModelScope.launch(Dispatchers.IO) {
        _locationState.value.location?.let { locPosition ->
            awaitAll(
                async { getSpotsFlow(context, directions, position, locPosition) },
                async { getAreasFlow(context, directions, position, locPosition) },
            )
        }
    }

    private suspend fun getSpotsFlow(
        context: Context,
        directions: Directions,
        position: Position,
        locPosition: Position
    ) {

        spotsUseCases.getSpotsUseCase(
            Triple(
                SPOT_COLLECTION_REFERENCE,
                context to directions,
                position to locPosition,
            )
        ).collectLatest { newSpotsUiState ->
            _spotsState.update {
                newSpotsUiState
            }
            if (newSpotsUiState.spots.isNotEmpty() && _spotState.value.tag.isEmpty()) {
                onSelectSpot(newSpotsUiState.spots.first().tag)
            }
        }
    }

    fun onSelectSpot(tag: String) {
        _spotState.update {
            _spotsState.value.spots.find { it.tag == tag }!!
        }
    }

    private suspend fun getAreasFlow(
        context: Context,
        directions: Directions,
        position: Position,
        locPosition: Position
    ) {
        spotsUseCases.getAreasUseCase(
            Triple(
                AREA_COLLECTION_REFERENCE,
                context to directions,
                position to locPosition,
            )
        ).collectLatest { newSpotsUiState ->
            _areasState.update {
                newSpotsUiState
            }
        }
    }

    //    SET SPOT
    fun onSet(
        camPosState: CameraPositionState,
        rememberHomeUiContainerState: HomeUiContainerState,
        onMainState: () -> Unit
    ) =
        viewModelScope.launch {
            if (!camPosState.isMoving && camPosState.position.zoom > 12f) {

//            _spotsState.update {
//                it.copy(isLoading = true)
//            }

                val currentPosition = Position(
                    camPosState.position.target.latitude,
                    camPosState.position.target.longitude
                )


//            FIXME: HANDLER THIS

                when (rememberHomeUiContainerState.screenState) {
                    ScreenState.ADDSPOT -> {

                        Spot().copy(
                            timestamp = selectTime(rememberHomeUiContainerState.indexSpotTime),
                            type = SpotType.BLUE,
                            directions = _directionsState.value.directions,
                            position = currentPosition,
                            user = _userState.value.user
                        ).let { spot ->
                            spotsUseCases.setSpotUseCase(SPOT_COLLECTION_REFERENCE to spot)
                        }.collectLatest { newHomeUiState ->
                            onMainState()
                            _spotsState.update {
                                it.copy(errorMessage = newHomeUiState.errorMessage)
                            }
                        }
                    }

                    ScreenState.PARKMYCAR -> {

                        _userState.value.user.copy(car = currentPosition).let { user ->
                            userRepository.setUserCar(user)
                        }.collectLatest { newHomeUiState ->
                            onMainState()
                            _userState.update {
                                it
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

    private fun selectTime(timerLengthSelection: Int): Long {
        val selectedInterval = when (timerLengthSelection) {
            0 -> TimeUnit.MILLISECONDS.convert(0, TimeUnit.MINUTES)
            1 -> TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
            2 -> TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)
            3 -> TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES)
            4 -> TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES)
            else -> TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES)
        }
        return currentTime() + selectedInterval
    }

    fun onRemoveSpot(spot: Spot) = viewModelScope.launch {
//        FIXME : THIS
        if (_userState.value.user.uid != spot.user.uid) {

            spotsUseCases.removeSpotUseCase(spot.copy(icon = null))
                .collectLatest { newHomeUiState ->
                    _spotsState.update {
                        newHomeUiState
                    }
                }

        } else {

        }
    }

    fun onSetPoint(points: Long) {
        spotsRepository.setPoints(uid = _userState.value.user.uid, incrementPoints = points)
    }
}

