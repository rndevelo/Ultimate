package com.rndeveloper.ultimate.ui.screens.permissions.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun PermissionsMainContent(
    description: String,
    onEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.permission_location),
            contentDescription = "Imagen de mapa con dos puntos",
            contentScale = ContentScale.Fit,
            modifier = modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        Text(
            text = stringResource(id = R.string.permissions_text_title),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Default
            )
        )
        Spacer(modifier = modifier.height(12.dp))
        Text(
            text = description,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.background
        )
        Spacer(modifier = modifier.height(24.dp))
        Box(
            modifier = modifier
                .clickable { onEvent() }
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFff669f),
                            Color(0xFFff8961)
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.permissions_text_activate),
                fontSize = 20.sp,
                color = Color.White
            )
        }
        Spacer(modifier = modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CustomDialogPermissionsPreview() {
    UltimateTheme {
        PermissionsMainContent(description = "", onEvent = {})
    }
}