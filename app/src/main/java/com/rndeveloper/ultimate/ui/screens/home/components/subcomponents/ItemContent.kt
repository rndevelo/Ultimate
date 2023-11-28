package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.extensions.getFormattedPrettyTime
import com.rndeveloper.ultimate.model.Spot

@Composable
fun ItemContent(
    spot: Spot,
    selectedSpot: Spot?,
    onSpotSelected: () -> Unit,
    onRemoveSpot: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val surfaceColor by animateColorAsState(
        targetValue = if (spot == selectedSpot)
            MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
        label = "",
    )

    val context = LocalContext.current

    var expandedItem by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSpotSelected() }
            .padding(2.dp),
//        shape = RoundedCornerShape(22.dp),
        color = surfaceColor,
//        border = BorderStroke(color = surfaceColor, width = 0.7.dp),
    ) {
        Column(
            modifier = modifier
                .padding(vertical = 2.dp, horizontal = 14.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
//                FIXME: Diseñar este icono en condiciones ostia menuda mierda
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_spot_type),
//                    contentDescription = "Tipo de plaza",
//                    tint = spotColor
//                )

                Text(text = "12m")

//                FIXME: Hay que pone estas fun de ext en el snapshot del repo pa q se actualice automaticamente
                Text(text = spot.timestamp.getFormattedPrettyTime())


                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(onClick = { expandedItem = !expandedItem }) {
                        Icon(
                            imageVector = if (expandedItem) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expandedItem) Icons.Filled.KeyboardArrowUp.toString() else Icons.Filled.KeyboardArrowDown.toString(),
                        )
                    }

                    FloatingActionButton(onClick = { goNavigate(latLng = LatLng(spot.position.lat, spot.position.lng), context = context) }) {
                        Icon(
                            imageVector = Icons.Filled.Navigation,
                            contentDescription = Icons.Filled.Navigation.toString(),
                        )
                    }
                }
            }

            if (expandedItem) {
                Text(
                    text = spot.directions.addressLine,
                    maxLines = 3
                )
                Text(text = "por ${spot.user.username}",)
            }
        }
    }
}

private fun goNavigate(latLng: LatLng?, context: Context) {
    if (latLng != null) {
        val navigationIntentUri =
            Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude) //creating intent with latlng

        val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        ContextCompat.startActivity(context, mapIntent, null)
    }
}