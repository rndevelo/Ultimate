package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.User

@Composable
fun ProfileHeaderContent(
    user: User,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = if (user.photo == "null") {
                    painterResource(id = R.drawable.user_photo)
                } else {
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = user.photo)
                            .allowHardware(false)
                            .build()
                    )
                },
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .size(45.dp)
                    .background(color = MaterialTheme.colorScheme.surface)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = stringResource(R.string.app_text_welcome))
                Text(text = user.username)
            }
        }
        Text(
            text = "${user.points}creds.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}

