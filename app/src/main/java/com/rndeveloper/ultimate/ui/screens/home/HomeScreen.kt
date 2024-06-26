package com.rndeveloper.ultimate.ui.screens.home

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.rndeveloper.ultimate.extensions.customSnackBar
import com.rndeveloper.ultimate.model.Item
import com.rndeveloper.ultimate.ui.screens.home.components.BottomBarContent
import com.rndeveloper.ultimate.ui.screens.home.components.DrawerContent
import com.rndeveloper.ultimate.ui.screens.home.components.MainContent
import com.rndeveloper.ultimate.ui.screens.home.components.SheetContent
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.LocationUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME
import com.rndeveloper.ultimate.utils.Constants.SPOTS_TIMER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    rememberHomeUiContainerState: HomeUiContainerState = rememberHomeUiContainerState(),
) {

    val uiLocationState by homeViewModel.uiLocationState.collectAsStateWithLifecycle()
    val uiUserState by homeViewModel.uiUserState.collectAsStateWithLifecycle()
    val uiSpotsState by homeViewModel.uiSpotsState.collectAsStateWithLifecycle()
    val uiSpotState by homeViewModel.uiItemState.collectAsStateWithLifecycle()
    val uiAreasState by homeViewModel.uiAreasState.collectAsStateWithLifecycle()
    val uiElapsedTimeState by homeViewModel.uiElapsedTimeState.collectAsStateWithLifecycle()
    val uiDirectionsState by homeViewModel.uiDirectionsState.collectAsStateWithLifecycle()
    val uiRouteState by homeViewModel.uiRouteState.collectAsStateWithLifecycle()

    HomeContent(
        onNavigate = navController::navigate,
        rememberHomeUiContainerState = rememberHomeUiContainerState,
        uiLocationState = uiLocationState,
        uiUserState = uiUserState,
        uiSpotsState = uiSpotsState,
        uiItemState = uiSpotState,
        uiAreasState = uiAreasState,
        uiElapsedTimeState = uiElapsedTimeState,
        uiDirectionsState = uiDirectionsState,
        uiRouteState = uiRouteState,
        onSet = homeViewModel::onSet,
        onSelectSpot = homeViewModel::onSelectSpot,
        onCreateRoute = homeViewModel::onCreateRoute,
        onRemoveSpot = homeViewModel::onRemoveSpot,
        onStartTimer = { homeViewModel.onSaveGetStartTimer(SPOTS_TIMER) },
        onGetAddressLine = homeViewModel::onGetAddressLine,
        showRewardedAdmob = homeViewModel::showRewardedAdmob,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    onNavigate: (String) -> Unit,
    rememberHomeUiContainerState: HomeUiContainerState,
    uiLocationState: LocationUiState,
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiItemState: Item?,
    uiAreasState: AreasUiState,
    uiElapsedTimeState: Long,
    uiDirectionsState: DirectionsUiState,
    uiRouteState: List<LatLng>,
    onSet: (HomeUiContainerState, () -> Unit) -> Unit,
    onSelectSpot: (String) -> Unit,
    onCreateRoute: () -> Unit,
    onRemoveSpot: (Context) -> Unit,
    onStartTimer: () -> Unit,
    onGetAddressLine: (Context, CameraPositionState, Boolean) -> Unit,
    showRewardedAdmob: (Context) -> Unit,
) {

    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = isFirstLaunch && uiLocationState.location != null && rememberHomeUiContainerState.isMapLoaded) {
        if (isFirstLaunch && uiLocationState.location != null && rememberHomeUiContainerState.isMapLoaded) {
            rememberHomeUiContainerState.onAnimateCamera(
                LatLng(uiLocationState.location.lat, uiLocationState.location.lng),
                15f,
                0f,
            )
            isFirstLaunch = false
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            !rememberHomeUiContainerState.camPosState.isMoving
                    && rememberHomeUiContainerState.camPosState.position.zoom >= 12f
        }.collect { doLoad ->
            onGetAddressLine(
                context,
                rememberHomeUiContainerState.camPosState,
                doLoad
            )
        }
    }

    LaunchedEffect(key1 = uiItemState) {
        if (!isFirstLaunch && uiItemState != null && uiElapsedTimeState > DEFAULT_ELAPSED_TIME && uiItemState.tag.isNotEmpty()) {

            rememberHomeUiContainerState.onAnimateCameraBounds(
                uiLocationState.location,
                uiItemState.position
            )
            rememberHomeUiContainerState.scrollState.animateScrollToItem(
                index = uiSpotsState.items.indexOf(uiSpotsState.items.find { it.tag == uiItemState.tag })
            )
            onCreateRoute()
        }
    }

    if (uiUserState.errorMessage?.error?.isNotEmpty() == true) {
        LaunchedEffect(snackBarHostState, uiUserState.errorMessage.error) {
            snackBarHostState.customSnackBar(uiUserState.errorMessage.error)
        }
    }

    if (uiSpotsState.errorMessage?.error?.isNotEmpty() == true) {
        LaunchedEffect(snackBarHostState, uiSpotsState.errorMessage.error) {
            snackBarHostState.customSnackBar(uiSpotsState.errorMessage.error)
        }
    }

    LaunchedEffect(key1 = Unit, key2 = uiElapsedTimeState > DEFAULT_ELAPSED_TIME) {
        rememberHomeUiContainerState.onOpenBottomSheet()
    }

    ModalNavigationDrawer(
        drawerState = rememberHomeUiContainerState.drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.8f)) {
                DrawerContent(uiUserState = uiUserState, onNavigate = onNavigate)
            }
        },
        gesturesEnabled = rememberHomeUiContainerState.drawerState.isOpen
    ) {
        Scaffold(
            bottomBar = {
                BottomBarContent(
                    rememberHomeUiContainerState = rememberHomeUiContainerState,
                    isElapsedTime = uiElapsedTimeState > DEFAULT_ELAPSED_TIME,
                    onStartTimer = onStartTimer,
                    onRemoveSpot = { onRemoveSpot(context) },
                    onSet = { onMain ->
                        onSet(rememberHomeUiContainerState, onMain)
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                BottomSheetScaffold(
                    sheetContent = {
                        SheetContent(
                            rememberHomeUiContainerState = rememberHomeUiContainerState,
                            uiSpotsState = uiSpotsState,
                            uiDirectionsState = uiDirectionsState,
                            uiElapsedTimeState = uiElapsedTimeState,
                            selectedItem = uiItemState,
                            onSelectSpot = onSelectSpot,
                        )
                    },
                    modifier = Modifier.padding(contentPadding),
                    scaffoldState = rememberHomeUiContainerState.bsScaffoldState,
                    sheetPeekHeight = 130.dp,
                    sheetShape = BottomSheetDefaults.HiddenShape,
                    sheetTonalElevation = 3.dp,
                    sheetSwipeEnabled = rememberHomeUiContainerState.screenState == ScreenState.MAIN
                ) {
                    MainContent(
                        rememberHomeUiContainerState = rememberHomeUiContainerState,
                        camPosState = rememberHomeUiContainerState.camPosState,
                        uiUserState = uiUserState,
                        uiSpotsState = uiSpotsState,
                        uiAreasState = uiAreasState,
                        uiElapsedTimeState = uiElapsedTimeState,
                        uiRouteState = uiRouteState,
                        onCameraLoc = {
                            uiLocationState.location?.let { loc ->
                                rememberHomeUiContainerState.onAnimateCamera(
                                    latLng = LatLng(
                                        loc.lat,
                                        loc.lng
                                    )
                                )
                            }
                        },
                        onCameraCar = {
                            uiUserState.user.car?.let { car ->
                                rememberHomeUiContainerState.onAnimateCamera(
                                    latLng = LatLng(
                                        car.lat,
                                        car.lng
                                    )
                                )
                            }
                        },
                        onCameraCarLoc = {
                            rememberHomeUiContainerState.onAnimateCameraBounds(
                                uiLocationState.location,
                                uiUserState.user.car
                            )
                        },
                        onCameraTilt = rememberHomeUiContainerState::onAnimateCameraTilt,
                        onSelectSpot = onSelectSpot,
                        onSet = { onMain ->
                            onSet(rememberHomeUiContainerState, onMain)
                        },
                        showRewardedAdmob = { showRewardedAdmob(context) },
                        modifier = Modifier.height(
                            (rememberHomeUiContainerState.bsScaffoldState.bottomSheetState.requireOffset()
                                    / LocalContext.current.resources.displayMetrics.density).dp
                        )
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


