package com.rndeveloper.ultimate.ui.screens.home

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.rndeveloper.ultimate.BuildConfig
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.backend.routes.RoutesApiService
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.extensions.onNavigate
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Help
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Item
import com.rndeveloper.ultimate.model.SpotType
import com.rndeveloper.ultimate.model.Timer
import com.rndeveloper.ultimate.repositories.ActivityTransitionRepo
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.GeofenceClient
import com.rndeveloper.ultimate.repositories.HelpRepository
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.TimerRepository
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.LocationUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
import com.rndeveloper.ultimate.usecases.spots.SpotsUseCases
import com.rndeveloper.ultimate.usecases.user.GetUserDataUseCase
import com.rndeveloper.ultimate.utils.Constants
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME
import com.rndeveloper.ultimate.utils.Constants.INTERVAL
import com.rndeveloper.ultimate.utils.Constants.SPOT_COLLECTION_REFERENCE
import com.rndeveloper.ultimate.utils.Constants.TIMER
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    activityTransitionClient: ActivityTransitionRepo,
    private val timerRepository: TimerRepository,
    private val helpRepository: HelpRepository,
    private val locationClient: LocationClient,
    private val spotsUseCases: SpotsUseCases,
    private val userRepository: UserRepository,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val geocoderRepository: GeocoderRepository,
    private val routesApiService: RoutesApiService,
    private val geofenceClient: GeofenceClient,
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

    private val _itemState = MutableStateFlow(Item())
    val uiItemState: StateFlow<Item?> = _itemState.asStateFlow().stateIn(
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

    private val _elapsedTimeState = MutableStateFlow(DEFAULT_ELAPSED_TIME)
    val uiElapsedTimeState: StateFlow<Long> = _elapsedTimeState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DEFAULT_ELAPSED_TIME,
    )

    private val _helpState = MutableStateFlow(false)
    val uiHelpState: StateFlow<Boolean> = _helpState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false,
    )

    private val _directionsState = MutableStateFlow(DirectionsUiState())
    val uiDirectionsState: StateFlow<DirectionsUiState> = _directionsState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DirectionsUiState(),
    )

    private val _routeState = MutableStateFlow(emptyList<LatLng>())
    val uiRouteState: StateFlow<List<LatLng>> = _routeState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    init {
        activityTransitionClient.startActivityTransition()

        viewModelScope.launch {
            awaitAll(
                async { onGetUserData() },
                async { onGetLocationData() },
                async { onGetHelpValue() },
                async { onGetAndStartTimer() },
            )
        }
    }

    fun onCreateRoute(context: Context) = viewModelScope.launch {
        val call = routesApiService.getRoute(
            BuildConfig.ROUTES_API_KEY,
            "${_locationState.value.location?.lng},${_locationState.value.location?.lat}",
            "${_itemState.value.position.lng},${_itemState.value.position.lat}"
        )
        if (call.isSuccessful) {
            if (call.body() != null) {
                val mapRoute = call.body()!!.features.first().geometry.coordinates.map {
                    LatLng(it[1], it[0])
                }
                _routeState.update {
                    mapRoute
                }
            } else {
                _routeState.update {
                    emptyList()
                }
                _spotsState.update {
                    it.copy(errorMessage = CustomException.GenericException(context.getString(R.string.home_text_the_route_could_not_be_calculated)))
                }
            }
        } else {
            _routeState.update {
                emptyList()
            }
            _spotsState.update {
                it.copy(errorMessage = CustomException.GenericException(context.getString(R.string.home_text_the_route_could_not_be_calculated)))
            }
        }
    }

    //    HELP
    private suspend fun onGetHelpValue() {
        helpRepository.getData(Constants.HELP_KEY).collectLatest { help ->
            _helpState.update {
                help.isHelp
            }
        }
    }

    fun onSetHelpValue(isHelp: Boolean) = viewModelScope.launch {
        Help(
            id = Constants.HELP_KEY,
            isHelp = isHelp
        ).let {
            helpRepository.saveData(it)
        }
    }

    private suspend fun onGetAndStartTimer() {
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
                        _routeState.update {
                            emptyList()
                        }
                    }
                }.start()
            }
        }
    }

    fun onSaveGetStartTimer(context: Context, timerId: String) {

        if (_spotsState.value.items.isNotEmpty()) {
            if (_userState.value.user.points >= 5) {
                val timer = Timer(
                    id = timerId,
                    endTime = currentTime() + TIMER
                )
                if (_elapsedTimeState.value <= DEFAULT_ELAPSED_TIME) {
                    viewModelScope.launch {
                        timerRepository.saveTimer(timer = timer)
                    }
                    onSetPoint(points = -5)
                }

            } else {
                _spotsState.update {
                    it.copy(errorMessage = CustomException.GenericException(context.getString(R.string.home_text_you_don_t_have_enough_credits)))
                }
            }

        } else {
            _spotsState.update {
                it.copy(errorMessage = CustomException.GenericException(context.getString(R.string.home_text_there_are_no_parking_in_this_area)))
            }
        }
    }

    private suspend fun onGetUserData() {
        getUserDataUseCase(_userState.value.user.uid).collectLatest { newUserUiState ->
            _userState.update {
                newUserUiState
            }
        }
    }

    private suspend fun onGetLocationData() {
        _locationState.update {
            it.copy(isLoading = true)
        }

        locationClient.getLocationsRequest().collectLatest { newLocation ->

            _locationState.update {
                it.copy(location = newLocation, isLoading = false)
            }

            _spotsState.update {
                it.copy(
                    items = _spotsState.value.items.map { spot ->
                        val distance = FloatArray(2)
                        _locationState.value.location?.let { it1 ->
                            Location.distanceBetween(
                                spot.position.lat, spot.position.lng,
                                it1.lat, it1.lng, distance
                            )
                        }
                        spot.copy(distance = "${distance[0].toInt()}m")
                    }
                )
            }
        }
    }

    fun onGetAddressLine(
        context: Context,
        camPosState: CameraPositionState,
        doLoad: Boolean,
    ) = viewModelScope.launch(Dispatchers.IO) {

        _spotsState.update {
            it.copy(isLoading = true)
        }

        if (doLoad) {

            val currentPosition = Position(
                camPosState.position.target.latitude,
                camPosState.position.target.longitude
            )

            val directions = async {
                geocoderRepository.getAddressList(
                    LatLng(
                        camPosState.position.target.latitude,
                        camPosState.position.target.longitude,
                    )
                ).first()
            }.await()

            _directionsState.update {
                it.copy(directions = directions, isLoading = false)
            }

            _locationState.value.location?.let { locPosition ->
                getSpotsFlow(
                    context,
                    directions,
                    currentPosition,
                    locPosition
                )
            }
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
            if (_itemState.value.tag.isEmpty()) {
                _itemState.update {
                    newSpotsUiState.items.first()
                }
                onCreateRoute(context)

            }
            _spotsState.update {
                newSpotsUiState
            }
        }
    }

    fun onSelectSpot(tag: String) {
        _spotsState.value.items.find { it.tag == tag }?.let { spot ->
            _itemState.update {
                spot
            }
        }
    }

    //    SET SPOT
    fun onSet(
        context: Context,
        rememberHomeUiContainerState: HomeUiContainerState,
        onMainState: () -> Unit,
    ) = viewModelScope.launch {

        _spotsState.update {
            it.copy(isLoading = true)
        }

        val camPosState = rememberHomeUiContainerState.camPosState
        if (!camPosState.isMoving && camPosState.position.zoom > 12f) {

            val currentPosition = Position(
                camPosState.position.target.latitude,
                camPosState.position.target.longitude
            )

            when (rememberHomeUiContainerState.screenState) {
                ScreenState.ADDSPOT -> {

                    Item().copy(
                        timestamp = selectTime(rememberHomeUiContainerState.indexSpotTime),
                        type = SpotType.BLUE,
                        directions = _directionsState.value.directions,
                        position = currentPosition,
                        user = _userState.value.user
                    ).let { spot ->
                        spotsUseCases.setSpotUseCase(SPOT_COLLECTION_REFERENCE to spot)
                    }.collectLatest {
                        onMainState()
                        _spotsState.update {
                            it.copy(
                                errorMessage = CustomException.GenericException(context.getString(R.string.home_text_sent))
                            )
                        }
                    }
                }

                ScreenState.PARKMYCAR -> {

                    _userState.value.user.copy(car = currentPosition).let { user ->
                        userRepository.setUserData(user)
                    }.collectLatest {
                        onMainState()
                    }
                }

                ScreenState.MAIN -> {}
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

    fun onRemoveSpot(context: Context) = viewModelScope.launch {

        _spotsState.update {
            it.copy(isLoading = true)
        }

        if (_userState.value.user.uid != _itemState.value.user.uid) {
            geofenceClient.startGeofence(_itemState.value.copy(icon = null)).collect {
                if (it.isSuccess) {
                    LatLng(
                        _itemState.value.position.lat,
                        _itemState.value.position.lng
                    ).onNavigate(context = context)
                } else {
                    _spotsState.update {
                        _spotsState.value.copy(
                            isLoading = false,
                            errorMessage = CustomException.GenericException(context.getString(R.string.home_text_start_geofence_was_error))
                        )
                    }
                }
            }
        } else {
            _spotsState.update {
                _spotsState.value.copy(
                    isLoading = false,
                    errorMessage = CustomException.GenericException(context.getString(R.string.home_text_you_can_t_get_your_own_place))
                )
            }
        }
    }

    fun onSetPoint(points: Long) = viewModelScope.launch {
        _userState.value.user.copy(points = _userState.value.user.points + points).let { user ->
            userRepository.setUserData(user)
        }.collectLatest { newHomeUiState -> }
    }

    fun onShowRewardedAdmob(context: Context) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            if (BuildConfig.DEBUG) {
                BuildConfig.ADMOB_REWARDED_ID
            } else {
                BuildConfig.ADMOB_REWARDED_ID_RELEASE
            },
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    _spotsState.update {
                        it.copy(errorMessage = CustomException.GenericException(adError.message))
                    }
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {

                    rewardedAd.show(context as Activity) { rewardItem ->
                        onSetPoint(1)
                        _spotsState.update {
                            it.copy(
                                errorMessage = CustomException.GenericException(
                                    context.getString(R.string.home_text_add_won_one_point)
                                )
                            )
                        }
                    }
                }
            })
    }
}

