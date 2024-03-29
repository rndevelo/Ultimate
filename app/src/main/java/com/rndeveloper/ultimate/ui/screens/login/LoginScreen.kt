package com.rndeveloper.ultimate.ui.screens.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
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
import com.rndeveloper.ultimate.ui.screens.login.components.EmailAndPasswordContent
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import kotlinx.coroutines.delay

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    val snackBarHostState = remember { SnackbarHostState() }
    val loginUiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = loginUiState.isLogged) {
        if (loginUiState.isLogged) {
            delay(1000)
            navController.navigate(Routes.PermissionsScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    if (!loginUiState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(snackBarHostState) {
            snackBarHostState.showSnackbar(
                loginUiState.errorMessage!!.error,
                "Close",
                false,
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
                uiLoginState = loginUiState,
                onClickLogin = viewModel::signInOrSignUp,
                onCLickRecoverPassword = viewModel::recoverPassword,
                onChangeScreenState = viewModel::changeScreenState,
                onClickGoogleButton = {
                    startForResult.launch(viewModel.googleSignInClient.signInIntent)
                }
            )
        }
    }
}

@Composable
fun LoginContent(
    uiLoginState: LoginUiState,
    onClickLogin: (String, String) -> Unit,
    onCLickRecoverPassword: (String) -> Unit,
    onChangeScreenState: () -> Unit,
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
                .align(if (!uiLoginState.isLogged) Alignment.TopCenter else Alignment.Center)
                .padding(22.dp),
        ) {
            Column(
                modifier = modifier
                    .padding(horizontal = 25.dp)
                    .padding(bottom = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ultimate_foreground),
                    contentDescription = R.drawable.ic_ultimate_foreground.toString(),
                    modifier = Modifier.size(160.dp)
                )
                Text(
                    text = "Bienvenido a Paparcar",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    modifier = modifier.padding(bottom = 6.dp)
                )

                AnimatedVisibility(uiLoginState.isLogged) {
                    Text(
                        text = "Esta es una aplicación basada en una gran comunidad de conductores en la que crecemos día a día",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                }
                if (!uiLoginState.isLogged) {
                    EmailAndPasswordContent(
                        uiLoginState = uiLoginState,
                        onChangeScreenState = onChangeScreenState,
                        onClickGoogleButton = onClickGoogleButton,
                        onClick = onClickLogin,
                    )
                }
            }
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