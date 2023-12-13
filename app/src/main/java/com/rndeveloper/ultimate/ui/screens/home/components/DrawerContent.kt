package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.rndeveloper.ultimate.ui.screens.home.UserUiState

@Composable
fun DrawerHeaderContent(uiUserState: UserUiState, modifier: Modifier = Modifier) {

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 35.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
            Text(text = "${uiUserState.user.points}ptos")
        }
        Divider(modifier = modifier.padding(horizontal = 15.dp))
        DrawerMenuItemContent(
            navigateToHistoryScreen = { /*TODO*/ },
            navigateToAccountScreen = { /*TODO*/ },
            uid = uiUserState.user.uid
        )
    }
}

@Composable
fun DrawerMenuItemContent(
    navigateToHistoryScreen: () -> Unit,
    navigateToAccountScreen: () -> Unit,
    uid: String?
) {
    val context = LocalContext.current
    DrawerMenuItemListContent(
        items = listOf(
            MenuItem(
//                id = 0,
                title = "Historial",
//                contentDescription = "Go to home screen",
                icon = Icons.Default.History,
//                color = Blue500,
                unit = { navigateToHistoryScreen() }
            ),
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
                title = "Cuenta",
//                contentDescription = "Get help",
                icon = Icons.Outlined.AccountCircle,
//                color = Blue500,
                unit = { navigateToAccountScreen() }
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

