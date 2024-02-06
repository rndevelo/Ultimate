package com.rndeveloper.ultimate.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rndeveloper.ultimate.extensions.getFormattedPrettyTime
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState

@Composable
fun HistoryScreen(
    navController: NavController,
    homeViewModel: HistoryViewModel = hiltViewModel(),
) {

    val uiUserState by homeViewModel.uiUserState.collectAsStateWithLifecycle()
    val uiHistoryState by homeViewModel.uiHistoryState.collectAsStateWithLifecycle()

    HistoryContent(uiUserState = uiUserState, uiHistoryState = uiHistoryState)
}

@Composable
fun HistoryContent(uiUserState: UserUiState, uiHistoryState: HistoryUiState) {

    val scrollState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {

        Text(text = uiUserState.user.username, modifier = Modifier.padding(7.dp))

        LazyColumn(state = scrollState) {

            items(items = uiHistoryState.history, key = { i -> i.tag }) { item ->
                HistoryItemContent(item = item) {

                }
            }
        }
    }
}

@Composable
fun HistoryItemContent(
    item: Spot,
    showSnackBar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 15.dp)
            .clickable {
//                navController.navigate(
//                    route = Routes.MarkerScreen.passId(
//                        item.address,
//                        item.lat.toString(),
//                        item.lng.toString()
//                    )
//                ) {
//                    launchSingleTop = true
//                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = /*if (item.isSelected == true) */Icons.Filled.People/* else Icons.Filled.Person*/,
            contentDescription = "Icon User",
//            tint = if (item.isSelected == true) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier
                .width(160.dp)
                .horizontalScroll(rememberScrollState()),
            text = item.directions.addressLine,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.timestamp.getFormattedPrettyTime(),
            color = Color.DarkGray,
            style = MaterialTheme.typography.labelMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "+${item.newpoints}",
            modifier = Modifier.clickable {
                showSnackBar()
            },
            color = /*if (item.isSelected == true) */MaterialTheme.colorScheme.error/* else MaterialTheme.colorScheme.primary*/,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.W500
            )
        )
    }
}
