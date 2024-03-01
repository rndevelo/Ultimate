package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PanoramaPhotosphere
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun ButtonsMapContent(
    car: Position?,
    isShowLoading: Boolean,
    onOpenOrCloseDrawer: () -> Unit,
    onMapType: () -> Unit,
    onCameraTilt: () -> Unit,
    onCameraLocation: () -> Unit,
    onCameraMyCar: () -> Unit,
    onCameraCarLoc: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val surfaceColor = MaterialTheme.colorScheme.surface
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(17.dp)
    ) {

        FloatingActionButton(
            modifier = modifier.align(Alignment.TopStart),
            onClick = onOpenOrCloseDrawer,
            containerColor = surfaceColor
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = Icons.Default.Menu.toString(),
            )
        }

        AnimatedVisibility(
            visible = isShowLoading,
            modifier = modifier.align(Alignment.TopCenter),
        ) {
            LoadingAnimation()
        }

        Column(modifier = modifier.align(Alignment.TopEnd)) {
            FloatingActionButton(
                onClick = onMapType,
                containerColor = surfaceColor
            ) {
                Icon(
                    imageVector = Icons.Default.Terrain,
                    contentDescription = Icons.Default.Terrain.toString(),
                )
            }
            Spacer(modifier = modifier.height(12.dp))
            FloatingActionButton(
                onClick = onCameraTilt,
                containerColor = surfaceColor
            ) {
                Icon(
                    imageVector = Icons.Default.PanoramaPhotosphere,
                    contentDescription = Icons.Default.PanoramaPhotosphere.toString(),
                )
            }
        }

        //        Admob button

        var isExpandedAdmobButton by remember { mutableStateOf(false) }

        FloatingActionButton(
            onClick = {
                showRewardedInterstitialAdmob(context = context)

            },
            modifier = Modifier.align(Alignment.BottomStart),

//            FIXME: this -----------------
            containerColor = MaterialTheme.colorScheme.tertiary

        ) {
            Row(modifier = modifier.padding(7.dp)) {

                Icon(
                    imageVector = Icons.Default.Slideshow,
                    contentDescription = Icons.Default.Slideshow.toString(),
                )
                AnimatedVisibility(visible = isExpandedAdmobButton) {
                    Spacer(modifier = modifier.width(5.dp))
                    Text(
                        text = "(+1cred.)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
                    )
                }

            }

        }

        Column(
            modifier = modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FloatingActionButton(
                modifier = modifier.size(40.dp),
                onClick = onCameraCarLoc,
                containerColor = surfaceColor

            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_carloc_userloc),
                    contentDescription = "Ir a Ubicación y coche"
                )
            }
            Spacer(modifier = modifier.height(4.dp))
            Row {
//                FloatingCar(
//                    car = car,
//                    onShowCarMarker = {},
//                    onCameraCar = {},
//                    deleteMyCar = {}
//                )
                FloatingActionButton(
                    modifier = modifier.size(40.dp),
                    onClick = onCameraMyCar,
                    containerColor = surfaceColor
                ) {
                    Row {
                        AnimatedVisibility(visible = car == null) {
                            Text(
                                text = stringResource(R.string.home_text_not_park_car),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = Icons.Default.DirectionsCar.toString(),
                        )
                    }
                }
                Spacer(modifier = modifier.width(8.dp))
                FloatingActionButton(
                    modifier = modifier.size(40.dp),
                    onClick = onCameraLocation,
                    containerColor = surfaceColor

                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = Icons.Default.GpsFixed.toString(),
                    )
                }
            }
        }
    }
}

fun showRewardedInterstitialAdmob(context: Context) {
    RewardedInterstitialAd.load(
        context,
        "ca-app-pub-9476899522712717/8060242202",
        AdRequest.Builder().build(),
        object : RewardedInterstitialAdLoadCallback() {
            override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                super.onAdLoaded(rewardedInterstitialAd)
                rewardedInterstitialAd.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            Toast.makeText(context, "onAdShowedFullScreenContent", Toast.LENGTH_LONG).show()
                        }
                    }
                rewardedInterstitialAd.show(context as Activity) { rewardItem ->
                    val amount = rewardItem.amount
                    val type = rewardItem.type
//                    showSnackBar()
                    Toast.makeText(context, "show", Toast.LENGTH_LONG).show()


                }
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
            }
        })
}

@Preview(showBackground = true)
@Composable
fun ButtonsMapContentPreview() {
    UltimateTheme {
        ButtonsMapContent(
            car = Position(),
            isShowLoading = true,
            onOpenOrCloseDrawer = { /*TODO*/ },
            onMapType = { /*TODO*/ },
            onCameraTilt = { /*TODO*/ },
            onCameraLocation = { /*TODO*/ },
            onCameraMyCar = {},
            onCameraCarLoc = { },
        )
    }
}