package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeveloper.ultimate.model.MenuItem
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ProfileHeaderContent
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState

@Composable
fun DrawerContent(
    uiUserState: UserUiState,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column {
        ProfileHeaderContent(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 35.dp),
            user = uiUserState.user
        )
        HorizontalDivider(modifier = modifier.padding(horizontal = 15.dp))
        DrawerMenuItemListContent(
            items = listOf(
                MenuItem(
//                id = 2,
                    title = "Settings",
//                contentDescription = "Get help",
                    icon = Icons.Outlined.Settings,
//                color = Blue500,
                    unit = { onNavigate(Routes.SettingsScreen.route) }
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

