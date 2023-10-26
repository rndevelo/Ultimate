package com.rndeveloper.ultimate.ui.screens.home.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.maps.model.LatLng

@Composable
fun BottomBarContent(
    isAddPanelState: Boolean,
    onAddPanelState: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(tonalElevation = 2.dp) {
        ExtendedFloatingActionButton(
            text = { Text(text = if (isAddPanelState) "Go back to" else "Add spot") },
            icon = {
                Icon(
                    imageVector = if (isAddPanelState) Icons.Default.ArrowBack else Icons.Default.AddLocationAlt,
                    contentDescription = if (isAddPanelState) Icons.Default.ArrowBack.toString() else Icons.Default.AddLocationAlt.toString(),
                )
            },
            onClick = onAddPanelState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            elevation = FloatingActionButtonDefaults.elevation(1.dp)
        )
    }

}


private fun goNavigate(latLng: LatLng?, context: Context) {
    if (latLng != null) {
        val navigationIntentUri =
            Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude) //creating intent with latlng

        val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(context, mapIntent, null)
    }
}