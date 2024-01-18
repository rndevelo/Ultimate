package com.rndeveloper.ultimate.ui.screens.home

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.components.BottomBarContent
import com.rndeveloper.ultimate.ui.screens.home.components.DrawerHeaderContent
import com.rndeveloper.ultimate.ui.screens.home.components.MainContent
import com.rndeveloper.ultimate.ui.screens.home.components.SheetContent
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants.SPOTS_TIMER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    rememberUiElementsState: HomeUiContainerState = rememberHomeUiContainerState(),
) {
    val uiUserState by homeViewModel.uiUserState.collectAsStateWithLifecycle()
    val uiSpotsState by homeViewModel.uiSpotsState.collectAsStateWithLifecycle()
    val uiElapsedTimeState by homeViewModel.uiElapsedTimeState.collectAsStateWithLifecycle()
    val uiDirectionsState by homeViewModel.uiDirectionsState.collectAsStateWithLifecycle()

    HomeContent(
        rememberHomeUiContainerState = rememberUiElementsState,
        uiUserState = uiUserState,
        uiSpotsState = uiSpotsState,
        uiElapsedTimeState = uiElapsedTimeState,
        uiDirectionsState = uiDirectionsState,
        onSet = homeViewModel::onSet,
        onRemoveSpot = homeViewModel::onRemoveSpot,
        onStartTimer = { homeViewModel.onSaveGetStartTimer(SPOTS_TIMER) },
        onSpotSelected = homeViewModel::onSpotSelected,
        onGetAddressLine = homeViewModel::onGetAddressLine,
        onCameraLoc = homeViewModel::onCameraLoc,
        onCameraCar = homeViewModel::onCameraCar,
        onCameraSpot = homeViewModel::onCameraSpot,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiElapsedTimeState: Long,
    uiDirectionsState: DirectionsUiState,
    onSet: (CameraPositionState, ScreenState) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    onStartTimer: () -> Unit,
    onSpotSelected: (String) -> Unit,
    onGetAddressLine: (CameraPositionState) -> Unit,
    onCameraLoc: (CameraPositionState) -> Unit,
    onCameraCar: (CameraPositionState) -> Unit,
    onCameraSpot: (CameraPositionState, () -> Unit) -> Unit,
) {

//    animateCamera when launch on first time
//    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }

    val camPosState: CameraPositionState = rememberCameraPositionState()
    val isMoving by remember { derivedStateOf { camPosState.isMoving } }

    LaunchedEffect(Unit) {
        snapshotFlow { !isMoving }.collect { _ ->
            onGetAddressLine(camPosState)
        }
    }

//    LaunchedEffect(uiSpotsState.selectedSpot) {
//        if (/*uiElapsedTimeState > DEFAULT_ELAPSED_TIME && */uiSpotsState.selectedSpot != null) {
//
//            onCameraSpot(camPosState) {
//                rememberHomeUiContainerState.scrollState.animateScrollToItem(
//                    index = uiSpotsState.spots.indexOf(uiSpotsState.selectedSpot)
//                )
//            }
//        }
//    }
//
//    LaunchedEffect(key1 = uiElapsedTimeState > DEFAULT_ELAPSED_TIME) {
//        if (uiElapsedTimeState > DEFAULT_ELAPSED_TIME) {
//            rememberHomeUiContainerState.bsScaffoldState.bottomSheetState.expand()
//        }
//    }


    ModalNavigationDrawer(
        drawerState = rememberHomeUiContainerState.drawerState,
        modifier = Modifier.alpha(
            when (uiUserState.isLoading) {
                true -> 0.4f
                false -> 1f
            },
        ),
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.8f)) {
                DrawerHeaderContent(uiUserState = uiUserState)
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
                    onSet = { onSet(camPosState, rememberHomeUiContainerState.screenState) }
                )
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                BottomSheetScaffold(
                    sheetContent = {
                        SheetContent(
                            rememberHomeUiContainerState = rememberHomeUiContainerState,
                            uiSpotsState = uiSpotsState,
                            uiDirectionsState = uiDirectionsState,
                            onExpand = {},
                            onSpotSelected = onSpotSelected,
                            onRemoveSpot = onRemoveSpot
                        )
                    },
                    modifier = Modifier.padding(contentPadding),
                    scaffoldState = rememberHomeUiContainerState.bsScaffoldState,
                    sheetPeekHeight = 125.dp,
                    sheetShape = BottomSheetDefaults.HiddenShape,
                    sheetTonalElevation = 2.dp,
//                    sheetSwipeEnabled = homeUiState.elapsedTime > 0L
                ) {
                    MainContent(
                        rememberHomeUiContainerState = rememberHomeUiContainerState,
                        camPosState = camPosState,
                        uiUserState = uiUserState,
                        uiSpotsState = uiSpotsState,
                        uiElapsedTimeState = uiElapsedTimeState,
                        onCameraLoc = { onCameraLoc(camPosState) },
                        onCameraCar = { onCameraCar(camPosState) },
                        onSpotSelected = onSpotSelected,
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


