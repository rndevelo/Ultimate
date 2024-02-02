package com.rndeveloper.ultimate.ui.screens.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState

@Composable
fun HistoryScreen(
    navController: NavController,
    homeViewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiUserState by homeViewModel.uiUserState.collectAsStateWithLifecycle()
    val uiHistoryState by homeViewModel.uiHistoryState.collectAsStateWithLifecycle()
    HistoryContent(uiUserState = uiUserState,uiHistoryState = uiHistoryState,)
}

@Composable
fun HistoryContent(uiUserState: UserUiState, uiHistoryState: HistoryUiState) {

    val scrollState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {

        Text(text = uiUserState.user.username, modifier = Modifier.padding(7.dp))

        LazyColumn(state = scrollState) {

            items(items = uiHistoryState.history, key = { i -> i.tag }) { item ->
                Text(text = item.timestamp.toString(), modifier = Modifier.padding(7.dp))
            }
        }
    }
}