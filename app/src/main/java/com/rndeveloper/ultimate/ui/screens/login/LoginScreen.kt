package com.rndeveloper.ultimate.ui.screens.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    val snackBarHostState = remember { SnackbarHostState() }
    val loginUiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = loginUiState) {
        if (loginUiState.isLogged) {
            navController.navigate(Routes.HomeScreen.route)
        }
    }

    LaunchedEffect(key1 = loginUiState.errorMessage) {
        if (!loginUiState.errorMessage?.error.isNullOrBlank()) {
            snackBarHostState.showSnackbar(
                "Firebase Message : ${loginUiState.errorMessage!!.error}",
                "Close",
                true,
                SnackbarDuration.Long
            )
        }
    }

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)
                    viewModel.handleGoogleSignInResult(task)
                }
            }
        }

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            LoginContent(
                loginUiState = loginUiState,
                onClickLogin = viewModel::signInOrSignUp,
                onCLickRecoverPassword = viewModel::recoverPassword,
                onClickScreenState = viewModel::changeScreenState,
                onClickGoogleButton = {
                    startForResult.launch(viewModel.googleSignInClient.signInIntent)
                }
            )
        }
    }
}

@Composable
fun LoginContent(
    loginUiState: LoginUiState,
    onClickLogin: (String, String) -> Unit,
    onCLickRecoverPassword: (String) -> Unit,
    onClickScreenState: () -> Unit,
    onClickGoogleButton: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.im_login_screen_spots),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 1f),
                            Color.White.copy(alpha = 0.4f)
                        )
                    )
                )
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(22.dp),
        ) {
            Column(
                modifier = modifier
                    .padding(20.dp)
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ultimate_foreground),
                    contentDescription = "Logo Login",
                    modifier = Modifier.size(180.dp)
                )
                Text(
                    text = "Bienvenido a Paparcar",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = "Esta es una aplicación basada en una gran comunidad de conductores en la que crecemos día a día",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = onClickGoogleButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    GoogleButtonContent(isLoading = loginUiState.isLoading)
                }
            }
        }
    }
}

@Composable
private fun GoogleButtonContent(isLoading: Boolean) {

    Row(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = if (isLoading) "Signing in..." else "Sign in with Google")
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(horizontal = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    UltimateTheme {
        LoginScreen(navController = NavController(LocalContext.current))
    }
}