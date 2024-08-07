package com.rndeveloper.ultimate.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.extensions.customSnackBar
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.screens.login.components.AlertRecoverPassDialog
import com.rndeveloper.ultimate.ui.screens.login.components.EmailAndPasswordContent
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginUiState
import com.rndeveloper.ultimate.ui.screens.login.uistates.RecoverPassUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import kotlinx.coroutines.delay

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    val snackBarHostState = remember { SnackbarHostState() }
    val loginUiState by viewModel.state.collectAsStateWithLifecycle()
    val recoverPassUIState by viewModel.recoverPassUIState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = loginUiState) {
        if (loginUiState.isLogged && loginUiState.isEmailVerified) {
            delay(1000)
            navController.navigate(Routes.PermissionsScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    if (!loginUiState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(key1 = snackBarHostState, key2 = loginUiState.errorMessage?.error) {
            snackBarHostState.customSnackBar(loginUiState.errorMessage!!.error)
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
                recoverPassUIState = recoverPassUIState,
                onClickLoginOrRegister = viewModel::signInOrSignUp,
                onCLickRecoverPassword = viewModel::recoverPassword,
                onChangeScreenState = viewModel::changeScreenState,
                onClickGoogleButton = { viewModel.handleSignIn(context = context) },
                onRecoveryPassUpdate = viewModel::updateRecoveryPasswordState,
            )
        }
    }
}

@Composable
fun LoginContent(
    uiLoginState: LoginUiState,
    recoverPassUIState: RecoverPassUiState,
    onClickLoginOrRegister: (String, String) -> Unit,
    onCLickRecoverPassword: (String) -> Unit,
    onChangeScreenState: () -> Unit,
    onClickGoogleButton: () -> Unit,
    onRecoveryPassUpdate: (RecoverPassUiState) -> Unit,
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
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ultimate_foreground),
                    contentDescription = R.drawable.ic_ultimate_foreground.toString(),
                    modifier = Modifier.size(160.dp)
                )
                Text(
                    text = stringResource(R.string.login_text_paparcar),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    modifier = modifier.padding(bottom = 6.dp)
                )

                AnimatedVisibility(uiLoginState.isLogged) {
                    Text(
                        text = stringResource(R.string.login_text_description),
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
                        onClickLoginOrRegister = onClickLoginOrRegister,
                        recoverPassUIState = recoverPassUIState,
                        onRecoveryPassUpdate = onRecoveryPassUpdate,
                    )
                }
            }
        }
    }
    if (recoverPassUIState.isDialogVisible) {
        AlertRecoverPassDialog(
            onCLickSend = onCLickRecoverPassword,
            recoverPassUIState = recoverPassUIState,
            dismissDialog = {
                onRecoveryPassUpdate(
                    recoverPassUIState.copy(
                        isDialogVisible = false,
                    ),
                )
            },
        )
    }
    if (uiLoginState.isEmailSent) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = { /*TODO*/ },
            title = { Text(stringResource(R.string.login_text_verify_email_title)) },
            text = { Text(stringResource(R.string.login_text_verify_email_text)) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = Icons.Default.Email.toString()
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    UltimateTheme {
        LoginContent(
            uiLoginState = LoginUiState(),
            recoverPassUIState = RecoverPassUiState(),
            onClickLoginOrRegister = { _, _ -> },
            onCLickRecoverPassword = {},
            onChangeScreenState = {},
            onClickGoogleButton = {},
            onRecoveryPassUpdate = {}
        )
    }
}