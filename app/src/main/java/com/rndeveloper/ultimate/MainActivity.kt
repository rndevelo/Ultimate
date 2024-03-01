package com.rndeveloper.ultimate

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.rndeveloper.ultimate.nav.NavGraph
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UltimateTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    tonalElevation = 3.dp
                ) {
                    NavGraph()
                }
            }
        }
        MobileAds.initialize(this)
//        loadRewardedInterstitialAdmob()
//        showRewardedInterstitialAdmob()
    }

    fun loadRewardedInterstitialAdmob() {
        RewardedInterstitialAd.load(
            this,
            "ca-app-pub-9476899522712717/5295026801",
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    super.onAdLoaded(rewardedInterstitialAd)
                    mRewardedInterstitialAd = rewardedInterstitialAd
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    mRewardedInterstitialAd = null
                }
            })
    }

    fun showRewardedInterstitialAdmob() {
        mRewardedInterstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Toast.makeText(
                        this@MainActivity,
                        "onAdShowedFullScreenContent",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    mRewardedInterstitialAd = null
                    loadRewardedInterstitialAdmob()
                }
            }
        mRewardedInterstitialAd?.show(this) { rewardItem ->
            val amount = rewardItem.amount
            val type = rewardItem.type
            //                    showSnackBar()
            Toast.makeText(this, "show", Toast.LENGTH_LONG).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    UltimateTheme { NavGraph() }
}