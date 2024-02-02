package com.rndeveloper.ultimate.ui.screens.home

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.model.SpotType
import com.rndeveloper.ultimate.model.Timer
import com.rndeveloper.ultimate.repositories.ActivityTransitionRepo
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.LocationClient
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
import com.rndeveloper.ultimate.utils.Constants.ITEM_COLLECTION_REFERENCE
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val locationClient: LocationClient,
    private val spotsUseCases: SpotsUseCases,
    private val userUseCases: UserUseCases,
    private val userRepository: UserRepository,
    private val activityTransitionClient: ActivityTransitionRepo,
    private val geocoderRepository: GeocoderRepository,
//    private val geofenceClient: GeofenceClient,
) : ViewModel() {

    private val _locationState = MutableStateFlow(LocationUiState())


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

    private var isTilt = false


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
            activityTransitionClient.startActivityTransition(user = newUserUiState.user)
        }
    }

    suspend fun onGetLocationData() {
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

//        if (loc != null) onCameraLoc(cameraPositionState)

    }

    fun onGetAddressLine(
        camPosState: CameraPositionState,
        screenState: ScreenState,
        doLoad: Boolean,
        isFirstLaunch: Boolean,
        onFirstLaunch: () -> Unit
    ) {

        _spotsState.update {
            it.copy(isLoading = true)
        }

        if (doLoad || isFirstLaunch) {

            val currentPosition = Position(
                camPosState.position.target.latitude,
                camPosState.position.target.longitude
            )

            geocoderRepository.getAddressList(LatLng(currentPosition.lat, currentPosition.lng)) { directions ->
                if (screenState == ScreenState.MAIN) {
                    onGetSpots(position = currentPosition, directions = directions)
                }
                _directionsState.update {
                    it.copy(directions = directions)
                }
            }

            onFirstLaunch()
        }
    }


    //    GET SPOTS
    private fun onGetSpots(
        position: Position,
        directions: Directions
    ) = viewModelScope.launch(Dispatchers.IO) {
        _locationState.value.location?.let { locPosition ->
            awaitAll(
                async { getSpotsFlow(directions, position, locPosition) },
                async { getAreasFlow(directions, position, locPosition) },
            )
        }
    }

    private suspend fun getSpotsFlow(
        directions: Directions,
        position: Position,
        locPosition: Position
    ) {
        spotsUseCases.getSpotsUseCase(
            Triple(
                ITEM_COLLECTION_REFERENCE,
                directions,
                position to locPosition,
            )
        ).collectLatest { newSpotsUiState ->
            _spotsState.update {
                newSpotsUiState
            }
        }
    }

    private suspend fun getAreasFlow(
        directions: Directions,
        position: Position,
        locPosition: Position
    ) {
        spotsUseCases.getAreasUseCase(
            Triple(
                AREA_COLLECTION_REFERENCE,
                directions,
                position to locPosition,
            )
        ).collectLatest { newSpotsUiState ->
            _areasState.update {
                newSpotsUiState
            }
        }
    }

    //    SET SPOT
    fun onSet(camPosState: CameraPositionState, screenState: ScreenState, onMainState: () -> Unit) =
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

                when (screenState) {
                    ScreenState.ADDSPOT -> {

                        Spot().copy(
                            timestamp = currentTime(),
                            type = SpotType.BLUE,
                            directions = _directionsState.value.directions,
                            position = currentPosition,
                            user = _userState.value.user
                        ).let { spot ->
                            spotsUseCases.setSpotUseCase(ITEM_COLLECTION_REFERENCE to spot)
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

    fun onRemoveSpot(spot: Spot) = viewModelScope.launch {
        spotsUseCases.removeSpotUseCase(spot).collectLatest { newHomeUiState ->
            _spotsState.update {
                newHomeUiState
            }
        }
    }


//ON CAMERA ANIMATE

    private fun onCamera(
        cameraPositionState: CameraPositionState,
        latLng: LatLng = cameraPositionState.position.target,
        zoom: Float = 15f,
        tilt: Float = cameraPositionState.position.tilt
    ) = viewModelScope.launch {
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition(latLng, zoom, tilt, 0f)
            )
        )
    }

    fun onCameraLoc(cameraPositionState: CameraPositionState) {
        _locationState.value.location?.let { position ->
            onCamera(cameraPositionState, LatLng(position.lat, position.lng))
        }
    }

    fun onCameraCar(cameraPositionState: CameraPositionState) {
        _userState.value.user.car?.let { position ->
            onCamera(cameraPositionState, LatLng(position.lat, position.lng))
        }
    }

    fun onCameraTilt(cameraPositionState: CameraPositionState) {
//      FIXME:  Handle this correctly
        isTilt = !isTilt
        onCamera(
            cameraPositionState = cameraPositionState,
            latLng = cameraPositionState.position.target,
            tilt = if (isTilt) 90f else 0f
        )
    }

    fun onCameraCarLoc(cameraPositionState: CameraPositionState) {

        val latLngBounds = LatLngBounds.Builder()
        _locationState.value.location?.let { latLngBounds.include(LatLng(it.lat, it.lng)) }
        _userState.value.user.car?.let { latLngBounds.include(LatLng(it.lat, it.lng)) }

        viewModelScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(
                    latLngBounds.build(), 250
                )
            )
        }
    }

    fun onCameraSpot(cameraPositionState: CameraPositionState, position: Position) {
        onCamera(cameraPositionState, LatLng(position.lat, position.lng))
    }

    fun onCameraZoom(cameraPositionState: CameraPositionState, zoom: Float) {
        onCamera(cameraPositionState = cameraPositionState, zoom = zoom)
    }
}

