package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rndeveloper.ultimate.model.MenuItem
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState

@Composable
fun DrawerHeaderContent(
    uiUserState: UserUiState,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 35.dp),
        ) {
            Row(
                modifier = modifier.align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = uiUserState.user.photo)
                            .allowHardware(false)
                            .build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .size(45.dp)
                    //.border(0.5.dp, Blue500, RoundedCornerShape(percent = 15))
                )
                Spacer(modifier = modifier.width(12.dp))
                Column {
                    Text(text = "Welcome,")
                    Text(text = uiUserState.user.username)
                }
            }
            Text(
                text = "${uiUserState.user.points}creds.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier.align(Alignment.TopEnd),
            )
        }
        HorizontalDivider(modifier = modifier.padding(horizontal = 15.dp))
        DrawerMenuItemContent(
            navigateToHistoryScreen = { onNavigate(Routes.HistoryScreen.route) },
            navigateToSettingsScreen = { onNavigate(Routes.SettingsScreen.route) },
            uid = uiUserState.user.uid
        )
    }
}

@Composable
fun DrawerMenuItemContent(
    navigateToHistoryScreen: () -> Unit,
    navigateToSettingsScreen: () -> Unit,
    uid: String?
) {
    val context = LocalContext.current
    DrawerMenuItemListContent(
        items = listOf(
            MenuItem(
//                id = 1,
                title = "Invita a tus amigos  +3",
//                contentDescription = "Invite Friends",
                icon = Icons.Outlined.GroupAdd,
//                color = Blue500,
                unit = {
                    uid?.let { it1 ->
//                        ShareDynamicLink().onShareClicked(
//                            context = context,
//                            uid = it1
//                        )
                    }
                }
            ),
            MenuItem(
//                id = 2,
                title = "Settings",
//                contentDescription = "Get help",
                icon = Icons.Outlined.Settings,
//                color = Blue500,
                unit = { navigateToSettingsScreen() }
            ),
            MenuItem(
//                id = 3,
                title = "Ayuda",
//                contentDescription = "Logout",
                icon = Icons.Outlined.Info,
//                color = Blue500,
                unit = {}
            ),
        )
    )
}


@Composable
fun DrawerMenuItemListContent(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 17.sp),
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(modifier = modifier) {
            items(items) { item ->
                Row(modifier = modifier
                    .fillMaxWidth()
                    .clickable { item.unit() }
                    .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
//                        color = MaterialTheme.colors.onSurface,
                        style = itemTextStyle,
                        modifier = modifier.weight(1f)
                    )
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "item.contentDescription",
//                        tint = MaterialTheme.colors.primary,
                    )
                }
            }
        }
    }
}

