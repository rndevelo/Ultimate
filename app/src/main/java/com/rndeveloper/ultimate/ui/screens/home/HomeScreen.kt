package com.rndeveloper.ultimate.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.extensions.onCamera
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.components.AddPanelContent
import com.rndeveloper.ultimate.ui.screens.home.components.BottomBarContent
import com.rndeveloper.ultimate.ui.screens.home.components.MainContent
import com.rndeveloper.ultimate.ui.screens.home.components.SheetContent
import com.rndeveloper.ultimate.utils.Constants.SPOTS_TIMER
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val uiHomeState by homeViewModel.uiHomeState.collectAsStateWithLifecycle()
    HomeContent(
        homeUiState = uiHomeState,
        onGetSpots = homeViewModel::getSpots,
        setSpot = homeViewModel::setSpot,
        onRemoveSpot = homeViewModel::removeSpot,
        onStartTimer = { homeViewModel.onSaveGetStartTimer(SPOTS_TIMER) },
        onSpotSelected = homeViewModel::onSpotSelected,
        onSetMyCar = homeViewModel::setMyCar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    homeUiState: HomeUiState,
    onGetSpots: (LatLng) -> Unit,
    setSpot: (LatLng) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    onStartTimer: () -> Unit,
    onSpotSelected: (String) -> Unit,
    onSetMyCar: (LatLng) -> Unit,
) {

    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scrollState: LazyListState = rememberLazyListState()


    val density = LocalContext.current.resources.displayMetrics.density

//    TODO: Save state to new launch
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 0f)
    }

    var isFirstLaunch by remember { mutableStateOf(true) }

    var isMapLoaded by remember { mutableStateOf(false) }
    var isTilt by remember { mutableStateOf(false) }
    var isAddPanelState by remember { mutableStateOf(false) }


//    animateCamera when launch on first time
    if (isFirstLaunch && homeUiState.loc != null) {
        LaunchedEffect(key1 = Unit) {
            cameraPositionState.onCamera(target = homeUiState.loc, zoom = 15f)
            if (cameraPositionState.position.zoom >= 15f) {
                delay(500)
                onGetSpots(homeUiState.loc)
                isFirstLaunch = false
            }
        }
    }

    if (homeUiState.elapsedTime > 0L && homeUiState.selectedSpot != null) {
        LaunchedEffect(key1 = homeUiState.selectedSpot) {
            cameraPositionState.onCamera(
                target = LatLng(
                    homeUiState.selectedSpot.position.lat,
                    homeUiState.selectedSpot.position.lng
                )
            )
            scrollState.animateScrollToItem(index = homeUiState.spots.indexOf(homeUiState.selectedSpot))
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
                    isAddPanelState = isAddPanelState,
                    onAddPanelState = { isAddPanelState = !isAddPanelState },
                )
            }
        ) { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {

                AnimatedVisibility(visible = isAddPanelState) {
                    AddPanelContent()
                }

                BottomSheetScaffold(
                    sheetContent = {
                        SheetContent(
                            homeUiState = homeUiState,
                            scrollState = scrollState,
                            onExpand = {},
                            onStartTimer = onStartTimer,
                            onSpotSelected = onSpotSelected,
                            onRemoveSpot = onRemoveSpot
                        )
                    },
                    scaffoldState = bottomSheetScaffoldState,
                    sheetPeekHeight = if (isAddPanelState) 0.dp else 120.dp,
                    sheetShape = BottomSheetDefaults.HiddenShape,
                    sheetTonalElevation = 2.dp,
                    sheetSwipeEnabled = !scrollState.isScrollInProgress
                ) {
                    MainContent(
                        homeUiState = homeUiState,
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = { isMapLoaded = true },
                        setSpot = setSpot,
                        onSetMyCar = onSetMyCar,
                        isAddPanelState = isAddPanelState,
                        onSpotSelected = onSpotSelected,
                        onOpenOrCloseDrawer = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        onCameraTilt = {
                            isTilt = !isTilt
                            scope.launch {
                                cameraPositionState.onCamera(
//                                FIXME :  Handle this correctly
                                    tilt = if (isTilt) 90f else 0f
                                )
                            }
                        },
                        onScreenState = {
                            isAddPanelState = !isAddPanelState
                        },
                        onCameraLocation = {
                            if (homeUiState.loc != null) {
                                scope.launch {
                                    cameraPositionState.onCamera(
                                        target = homeUiState.loc,
                                        zoom = 15f,
                                        tilt = 0f,
                                        bearing = 0f
                                    )
                                }
                            }
                        },
                        onGetSpots = {
                            if (!cameraPositionState.isMoving) {
                                onGetSpots(cameraPositionState.position.target)
                            }
                        },
                        modifier = Modifier.height((bottomSheetScaffoldState.bottomSheetState.requireOffset() / density).dp)
                    )
                }
            }
        }
    }
}


