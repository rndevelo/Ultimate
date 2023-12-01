package com.rndeveloper.ultimate.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.extensions.onCamera
import com.rndeveloper.ultimate.model.Car
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.components.AddPanelContent
import com.rndeveloper.ultimate.ui.screens.home.components.BottomBarContent
import com.rndeveloper.ultimate.ui.screens.home.components.MainContent
import com.rndeveloper.ultimate.ui.screens.home.components.SheetContent
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME
import com.rndeveloper.ultimate.utils.Constants.SPOTS_TIMER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val uiUserState by homeViewModel.uiUserState.collectAsStateWithLifecycle()
    val uiSpotsState by homeViewModel.uiSpotsState.collectAsStateWithLifecycle()
    val uiElapsedTimeState by homeViewModel.uiElapsedTimeState.collectAsStateWithLifecycle()
    val uiAddressLineState by homeViewModel.uiAddressLineState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    HomeContent(
        uiUserState = uiUserState,
        uiSpotsState = uiSpotsState,
        uiElapsedTimeState = uiElapsedTimeState,
        uiAddressLineState = uiAddressLineState,
        scope = scope,
        onGetSpots = homeViewModel::onGetSpots,
        onSetSpot = homeViewModel::onSetSpot,
        onRemoveSpot = homeViewModel::onRemoveSpot,
        onStartTimer = { scope.launch { homeViewModel.onSaveGetStartTimer(SPOTS_TIMER) } },
        onSpotSelected = homeViewModel::onSpotSelected,
        onSetMyCar = homeViewModel::onSetMyCar,
        onGetAddressLine = homeViewModel::onGetAddressLine,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiElapsedTimeState: Long,
    uiAddressLineState: String,
    scope: CoroutineScope,
    onGetSpots: (Position) -> Unit,
    onSetSpot: (Position) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    onStartTimer: () -> Unit,
    onSpotSelected: (String) -> Unit,
    onSetMyCar: (Car) -> Unit,
    onGetAddressLine: (LatLng) -> Unit,
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scrollState = rememberLazyListState()

    val density = LocalContext.current.resources.displayMetrics.density

//    TODO: Save state to new launch
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 0f)
    }

    var isFirstLaunch by remember { mutableStateOf(true) }

    var isMapLoaded by remember { mutableStateOf(false) }
    var isTilt by remember { mutableStateOf(false) }
    var isAddPanelState by remember { mutableStateOf(false) }
    var isParkMyCarState by remember { mutableStateOf(false) }

//    animateCamera when launch on first time
    if (isFirstLaunch && uiUserState.user.loc != null) {
        LaunchedEffect(key1 = Unit) {
            cameraPositionState.onCamera(
                target = LatLng(
                    uiUserState.user.loc.lat,
                    uiUserState.user.loc.lng
                ),
                zoom = 15f
            )
            if (cameraPositionState.position.zoom >= 15f) {
                delay(500)
                onGetSpots(uiUserState.user.loc)
                isFirstLaunch = false
            }
        }
    }

    if (!cameraPositionState.isMoving) {
        LaunchedEffect(key1 = Unit) {
            onGetAddressLine(cameraPositionState.position.target)
        }
    }

    if (uiElapsedTimeState > DEFAULT_ELAPSED_TIME && uiSpotsState.selectedSpot != null) {
        LaunchedEffect(key1 = uiSpotsState.selectedSpot) {
            awaitAll(
                async {
                    cameraPositionState.onCamera(
                        target = LatLng(
                            uiSpotsState.selectedSpot.position.lat,
                            uiSpotsState.selectedSpot.position.lng
                        )
                    )
                },
                async {
                    scrollState.animateScrollToItem(index = uiSpotsState.spots.indexOf(uiSpotsState.selectedSpot))
                }
            )
        }
    }

    LaunchedEffect(key1 = uiElapsedTimeState > 0L) {
        if (uiElapsedTimeState > 0L) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { /* Drawer content */ }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            bottomBar = {
                BottomBarContent(
                    isAddSpotPanelState = isAddPanelState,
                    isParkMyCarPanelState = isParkMyCarState,
                    onAddPanelState = { bool ->
                        if (uiElapsedTimeState < 1000L) isAddPanelState = bool
                    },
                    onParkMyCarState = {
                        if (uiElapsedTimeState < 1000L) isParkMyCarState = !isParkMyCarState
                    },
                    onSetSpot = {
                        if (!cameraPositionState.isMoving && cameraPositionState.position.zoom > 12f) {
                            onSetSpot(
                                Position(
                                    cameraPositionState.position.target.latitude,
                                    cameraPositionState.position.target.longitude
                                )
                            )
                        }
                    },
                    onSetMyCar = {
                        if (!cameraPositionState.isMoving && cameraPositionState.position.zoom > 12f) {
                            onSetMyCar(
                                Car(
                                    cameraPositionState.position.target.latitude,
                                    cameraPositionState.position.target.longitude
                                )
                            )
                        }
                    }
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .alpha(
                        when (uiUserState.isLoading) {
                            true -> 0.4f
                            false -> 1f
                        },
                    ),
            ) {

                AnimatedVisibility(visible = isAddPanelState) {
                    AddPanelContent(addressLine = uiAddressLineState)
                }

                BottomSheetScaffold(
                    sheetContent = {
                        SheetContent(
                            uiSpotsState = uiSpotsState,
                            uiElapsedTimeState = uiElapsedTimeState,
                            uiAddressLineState = uiAddressLineState,
                            scrollState = scrollState,
                            onExpand = {},
                            onStartTimer = onStartTimer,
                            onSpotSelected = onSpotSelected,
                            onRemoveSpot = onRemoveSpot
                        )
                    },
                    scaffoldState = bottomSheetScaffoldState,
                    sheetPeekHeight = if (isAddPanelState || isParkMyCarState) 0.dp else 110.dp,
                    sheetShape = BottomSheetDefaults.HiddenShape,
                    sheetTonalElevation = 2.dp,
//                    sheetSwipeEnabled = homeUiState.elapsedTime > 0L
                ) {
                    MainContent(
                        uiUserState = uiUserState,
                        uiSpotsState = uiSpotsState,
                        uiElapsedTimeState = uiElapsedTimeState,
                        isAddPanelState = isAddPanelState,
                        isParkMyCarState = isParkMyCarState,
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = { isMapLoaded = true },
                        onParkMyCarState = { isParkMyCarState = !isParkMyCarState },
                        onSetMyCar = onSetMyCar,
                        onSpotSelected = onSpotSelected,
                        onOpenOrCloseDrawer = { scope.launch { drawerState.open() } },
                        onCameraTilt = {
                            isTilt = !isTilt
                            scope.launch {
                                cameraPositionState.onCamera(
//                                FIXME :  Handle this correctly
                                    tilt = if (isTilt) 90f else 0f
                                )
                            }
                        },
                        onCameraLocation = {
                            uiUserState.user.loc?.let { position ->
                                scope.launch {
                                    cameraPositionState.onCamera(
                                        target = LatLng(
                                            position.lat,
                                            position.lng
                                        ),
                                        zoom = 15f,
                                        tilt = 0f,
                                        bearing = 0f
                                    )
                                }
                            }
                        },
                        onCameraMyCar = {
                            uiUserState.user.car?.let { position ->
                                scope.launch {
                                    cameraPositionState.onCamera(
                                        target = LatLng(
                                            position.lat,
                                            position.lng
                                        ),
                                        zoom = 15f,
                                        tilt = 0f,
                                        bearing = 0f
                                    )
                                }
                            }
                        },
                        onGetSpots = {
                            if (!cameraPositionState.isMoving && cameraPositionState.position.zoom > 12f) {
                                onGetSpots(
                                    Position(
                                        cameraPositionState.position.target.latitude,
                                        cameraPositionState.position.target.longitude
                                    )
                                )
                            }
                        },
                        modifier = Modifier.height((bottomSheetScaffoldState.bottomSheetState.requireOffset() / density).dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    UltimateTheme {
        HomeScreen(navController = NavController(LocalContext.current))
    }
}


