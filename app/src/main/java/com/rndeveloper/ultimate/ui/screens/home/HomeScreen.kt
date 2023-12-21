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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.maps.android.compose.CameraMoveStartedReason
import com.rndeveloper.ultimate.model.Position
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

    val uiLocState by homeViewModel.uiLocationState.collectAsStateWithLifecycle()
    val uiUserState by homeViewModel.uiUserState.collectAsStateWithLifecycle()
    val uiSpotsState by homeViewModel.uiSpotsState.collectAsStateWithLifecycle()
    val uiElapsedTimeState by homeViewModel.uiElapsedTimeState.collectAsStateWithLifecycle()
    val uiDirectionsState by homeViewModel.uiDirectionsState.collectAsStateWithLifecycle()

    HomeContent(
        rememberHomeUiContainerState = rememberUiElementsState,
        uiLocState = uiLocState,
        uiUserState = uiUserState,
        uiSpotsState = uiSpotsState,
        uiElapsedTimeState = uiElapsedTimeState,
        uiDirectionsState = uiDirectionsState,
        onSet = homeViewModel::onSet,
        onRemoveSpot = homeViewModel::onRemoveSpot,
        onStartTimer = { homeViewModel.onSaveGetStartTimer(SPOTS_TIMER) },
        onSpotSelected = homeViewModel::onSpotSelected,
        onGetAddressLine = homeViewModel::onGetAddressLine,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiLocState: LocationUiState,
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiElapsedTimeState: Long,
    uiDirectionsState: DirectionsUiState,
    onSet: (Position, ScreenState) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    onStartTimer: () -> Unit,
    onSpotSelected: (String) -> Unit,
    onGetAddressLine: (Position) -> Unit,
) {


//    animateCamera when launch on first time
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(!rememberHomeUiContainerState.camPosState.isMoving) {
        if (!rememberHomeUiContainerState.camPosState.isMoving && rememberHomeUiContainerState.camPosState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            // Do your work here, it will be done only when the map starts moving from a drag gesture.
            onGetAddressLine(
                Position(
                    rememberHomeUiContainerState.camPosState.position.target.latitude,
                    rememberHomeUiContainerState.camPosState.position.target.longitude
                )
            )
        }
    }

//    if (uiElapsedTimeState > DEFAULT_ELAPSED_TIME && uiSpotsState.selectedSpot != null) {
//        LaunchedEffect(key1 = uiSpotsState.selectedSpot) {
//            awaitAll(
//                async {
//                    rememberHomeUiContainerState.onCamera(target = uiSpotsState.selectedSpot.position)
//                },
//                async {
//                    rememberHomeUiContainerState.scrollState.animateScrollToItem(
//                        index = uiSpotsState.spots.indexOf(uiSpotsState.selectedSpot)
//                    )
//                }
//            )
//        }
//    }
//
//    if (uiElapsedTimeState > DEFAULT_ELAPSED_TIME) {
//        LaunchedEffect(key1 = Unit) {
//            rememberHomeUiContainerState.bsScaffoldState.bottomSheetState.expand()
//        }
//    } else {
////        LaunchedEffect(key1 = Unit) {
////            rememberHomeUiContainerState.bsScaffoldState.bottomSheetState.hide()
////        }
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
                    onStartTimer =  onStartTimer,
                    onSet = {
                        if (!rememberHomeUiContainerState.camPosState.isMoving && rememberHomeUiContainerState.camPosState.position.zoom > 12f) {
                            onSet(
                                Position(
                                    rememberHomeUiContainerState.camPosState.position.target.latitude,
                                    rememberHomeUiContainerState.camPosState.position.target.longitude
                                ),
                                rememberHomeUiContainerState.screenState
                            )
                        }
                    }
                )
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                BottomSheetScaffold(
                    sheetContent = {
                        SheetContent(
                            screenState = rememberHomeUiContainerState.screenState,
                            uiSpotsState = uiSpotsState,
                            uiDirectionsState = uiDirectionsState,
                            scrollState = rememberHomeUiContainerState.scrollState,
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
                        uiLocState = uiLocState,
                        uiUserState = uiUserState,
                        uiSpotsState = uiSpotsState,
                        uiElapsedTimeState = uiElapsedTimeState,
                        onMapLoaded = {
                            if (uiLocState.location != null) {
                                rememberHomeUiContainerState.onCamera(
                                    target = uiLocState.location,
                                    zoom = 16f
                                )
                            }
                        },
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


