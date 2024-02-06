package com.rndeveloper.ultimate.ui.screens.home

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rndeveloper.ultimate.model.SpotType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberHomeUiContainerState(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    bsScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    scrollState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): HomeUiContainerState = remember {
    HomeUiContainerState(
//        camPosState,
        drawerState,
        bsScaffoldState,
        scrollState,
        scope,
        navController
    )
}

@Stable
class HomeUiContainerState @OptIn(ExperimentalMaterial3Api::class) constructor(
//    val camPosState: CameraPositionState,
    val drawerState: DrawerState,
    val bsScaffoldState: BottomSheetScaffoldState,
    val scrollState: LazyListState,
    private val scope: CoroutineScope,
    val navController: NavHostController,
) {

    var screenState by mutableStateOf(ScreenState.MAIN)
        private set


    // UI logic
    val isSetState: Boolean
        get() = screenState == ScreenState.ADDSPOT || screenState == ScreenState.PARKMYCAR


    var indexSpotTime by mutableIntStateOf(0)

    var indexSpotType by mutableIntStateOf(0)

    fun onSpotTime(spotTime: Int) {
        indexSpotTime = spotTime
    }
    fun onSpotType(spotType: Int){
        indexSpotType = spotType
    }

    fun onOpenDrawer() {
        scope.launch { drawerState.open() }
    }

    fun onScreenState(newScreenState: ScreenState) {
        screenState = newScreenState
    }
}

enum class ScreenState {
    MAIN, ADDSPOT, PARKMYCAR
}