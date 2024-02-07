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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.components.BottomBarContent
import com.rndeveloper.ultimate.ui.screens.home.components.DrawerHeaderContent
import com.rndeveloper.ultimate.ui.screens.home.components.MainContent
import com.rndeveloper.ultimate.ui.screens.home.components.SheetContent
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME
import com.rndeveloper.ultimate.utils.Constants.SPOTS_TIMER
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    rememberUiElementsState: HomeUiContainerState = rememberHomeUiContainerState(),
) {
    val uiUserState by homeViewModel.uiUserState.collectAsStateWithLifecycle()
    val uiSpotsState by homeViewModel.uiSpotsState.collectAsStateWithLifecycle()
    val uiAreasState by homeViewModel.uiAreasState.collectAsStateWithLifecycle()
    val uiElapsedTimeState by homeViewModel.uiElapsedTimeState.collectAsStateWithLifecycle()
    val uiDirectionsState by homeViewModel.uiDirectionsState.collectAsStateWithLifecycle()

    HomeContent(
        onNavigate = navController::navigate,
        rememberHomeUiContainerState = rememberUiElementsState,
        uiUserState = uiUserState,
        uiSpotsState = uiSpotsState,
        uiAreasState = uiAreasState,
        uiElapsedTimeState = uiElapsedTimeState,
        uiDirectionsState = uiDirectionsState,
        onSet = homeViewModel::onSet,
        onRemoveSpot = homeViewModel::onRemoveSpot,
        onStartTimer = { homeViewModel.onSaveGetStartTimer(SPOTS_TIMER) },
        onGetAddressLine = homeViewModel::onGetAddressLine,
        onCameraLoc = homeViewModel::onCameraLoc,
        onCameraCar = homeViewModel::onCameraCar,
        onCameraCarLoc = homeViewModel::onCameraCarLoc,
        onCameraTilt = homeViewModel::onCameraTilt,
        onCameraSpot = homeViewModel::onCameraSpot,
        onCameraZoom = homeViewModel::onCameraZoom,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    onNavigate: (String) -> Unit,
    rememberHomeUiContainerState: HomeUiContainerState,
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiAreasState: AreasUiState,
    uiElapsedTimeState: Long,
    uiDirectionsState: DirectionsUiState,
    onSet: (CameraPositionState, HomeUiContainerState, () -> Unit) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    onStartTimer: () -> Unit,
    onGetAddressLine: (Context, CameraPositionState, ScreenState, Boolean, Boolean, () -> Unit) -> Unit,
    onCameraLoc: (CameraPositionState) -> Unit,
    onCameraCar: (CameraPositionState) -> Unit,
    onCameraCarLoc: (CameraPositionState) -> Unit,
    onCameraTilt: (CameraPositionState) -> Unit,
    onCameraSpot: (CameraPositionState, Position?) -> Unit,
    onCameraZoom: (CameraPositionState, Float) -> Unit,
) {

    val camPosState: CameraPositionState = rememberCameraPositionState()
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    var selectedSpot by rememberSaveable { mutableStateOf(uiSpotsState.spots.firstOrNull()) }


    LaunchedEffect(Unit) {
        snapshotFlow {
            !camPosState.isMoving || isFirstLaunch
        }.collect { doLoad ->
            onGetAddressLine(
                context,
                camPosState,
                rememberHomeUiContainerState.screenState,
                doLoad,
                isFirstLaunch
            ) {
                isFirstLaunch = false
            }
        }
    }

    if (!uiUserState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(uiUserState.errorMessage) {
            snackBarHostState.showSnackbar(
                uiSpotsState.errorMessage!!.error,
                "Close",
                true,
                SnackbarDuration.Long,
            )
        }
    }

    if (!uiSpotsState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(uiSpotsState.errorMessage) {
            snackBarHostState.showSnackbar(
                uiSpotsState.errorMessage!!.error,
                "Close",
                true,
                SnackbarDuration.Long,
            )
        }
    }

    LaunchedEffect(key1 = Unit, key2 = uiElapsedTimeState > DEFAULT_ELAPSED_TIME) {
        rememberHomeUiContainerState.bsScaffoldState.bottomSheetState.expand()
    }

    ModalNavigationDrawer(
        drawerState = rememberHomeUiContainerState.drawerState,
        modifier = Modifier.alpha(
            when (uiUserState.isLoading) {
                true -> 0.7f
                false -> 1f
            },
        ),
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.8f)) {
                DrawerHeaderContent(uiUserState = uiUserState, onNavigate = onNavigate)
            }
        },
        gesturesEnabled = rememberHomeUiContainerState.drawerState.isOpen
    ) {
        Scaffold(
            bottomBar = {
                BottomBarContent(
                    rememberHomeUiContainerState = rememberHomeUiContainerState,
                    uiElapsedTimeState = uiElapsedTimeState,
                    onStartTimer = onStartTimer,
                    onCameraZoom = { zoom ->
                        onCameraZoom(camPosState, zoom)
                    },
                    onSet = { onMain ->
                        onSet(camPosState, rememberHomeUiContainerState, onMain)
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
                            uiAreasState = uiAreasState,
                            uiDirectionsState = uiDirectionsState,
                            isElapsedTime = uiElapsedTimeState > DEFAULT_ELAPSED_TIME,
                            selectedSpot = selectedSpot,
                            onExpand = {},
                            onSpot = { tag ->
                                selectedSpot = uiSpotsState.spots.find {
                                    it.tag == tag
                                }
                                selectedSpot?.let { onCameraSpot(camPosState, it.position) }
                            },
                            onRemoveSpot = onRemoveSpot
                        )
                    },
                    modifier = Modifier.padding(contentPadding),
                    scaffoldState = rememberHomeUiContainerState.bsScaffoldState,
                    sheetPeekHeight = 125.dp,
                    sheetShape = BottomSheetDefaults.HiddenShape,
                    sheetTonalElevation = 2.dp,
                    sheetSwipeEnabled = rememberHomeUiContainerState.screenState == ScreenState.MAIN
                ) {
                    MainContent(
                        rememberHomeUiContainerState = rememberHomeUiContainerState,
                        camPosState = camPosState,
                        uiUserState = uiUserState,
                        uiSpotsState = uiSpotsState,
                        uiAreasState = uiAreasState,
                        uiElapsedTimeState = uiElapsedTimeState,
                        onCameraLoc = { onCameraLoc(camPosState) },
                        onCameraCar = { onCameraCar(camPosState) },
                        onCameraCarLoc = { onCameraCarLoc(camPosState) },
                        onCameraTilt = { onCameraTilt(camPosState) },
                        onSpot = { tag ->
                            selectedSpot = uiSpotsState.spots.find {
                                it.tag == tag
                            }
                            scope.launch {
                                rememberHomeUiContainerState.scrollState.animateScrollToItem(
                                    index = uiSpotsState.spots.indexOf(selectedSpot)
                                )
                            }
                        },
                        modifier = Modifier.height(
                            (rememberHomeUiContainerState.bsScaffoldState.bottomSheetState.requireOffset() / LocalContext.current.resources.displayMetrics.density).dp
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


