package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.CameraPositionState
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.components.HomeMainContent
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.timeList

@Composable
fun SetAlertDialog(
    rememberHomeUiContainerState: HomeUiContainerState,
    onSet: (onMain: () -> Unit) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            rememberHomeUiContainerState.onVisibleAlertDialog(false)
        },
        confirmButton = {
            Button(
                onClick = {
                    onSet {
                        rememberHomeUiContainerState.onVisibleAlertDialog(false)
                        rememberHomeUiContainerState.onScreenState(ScreenState.MAIN)
                        rememberHomeUiContainerState.onAnimateCamera(zoom = 16f)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.app_text_send))
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = timeList.first().icon,
                    contentDescription = timeList.first().icon.toString(),
                    tint = animateColorAsState(
                        timeList[rememberHomeUiContainerState.indexSpotTime].color,
                        label = ""
                    ).value
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(R.string.home_text_add_spot_time),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                DropDownMenuContent(
                    items = timeList,
                    index = rememberHomeUiContainerState.indexSpotTime,
                    onIndex = { index ->
                        rememberHomeUiContainerState.onSpotTime(index)
                    },
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    UltimateTheme {
        SetAlertDialog(rememberHomeUiContainerState = rememberHomeUiContainerState()) {

        }
    }
}