package com.rndeveloper.ultimate.ui.screens.privatepolicy

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.extensions.findActivity
import com.rndeveloper.ultimate.nav.Routes

@Composable
fun PrivacyPolicyScreen(
    navController: NavHostController, viewModel: PrivacyPolicyViewModel = hiltViewModel()
) {
    val privatePolicyUiState by viewModel.uiPrivatePolicyState.collectAsStateWithLifecycle()
    var isCheck by rememberSaveable { mutableStateOf(false) }
    val queryUrl: Uri =
        Uri.parse("https://www.termsfeed.com/live/dd825ee1-8785-4a77-ab97-537543aff59b")
    val intent = Intent(Intent.ACTION_VIEW, queryUrl)
    val context = LocalContext.current

    LaunchedEffect(key1 = privatePolicyUiState) {
        if (privatePolicyUiState) {
            navController.navigate(Routes.LoginScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.im_privacy_policy),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Text(
                text = stringResource(id = R.string.login_text_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }


        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Row(
                modifier = Modifier.padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isCheck, onCheckedChange = { isCheck = !isCheck })
                Column {
                    Text(
                        text = stringResource(R.string.privacy_policy_text_agree_to_the_terms_of_the),
                        fontSize = 15.sp
                    )
                    Text(
                        text = stringResource(R.string.privacy_policy_text_privacy_policy),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            context.findActivity()?.startActivity(intent)
                        }
                    )
                }
            }

            Button(
                onClick = { viewModel.onSetPrivatePolicyValue(isCheck) },
                enabled = isCheck,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(text = stringResource(R.string.privacy_policy_text_continue))
            }
        }
    }
}