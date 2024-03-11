package com.rndeveloper.ultimate.ui.screens.login.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.login.LoginUiState

@Composable
fun EmailAndPasswordContent(
    uiLoginState: LoginUiState,
    onChangeScreenState: () -> Unit,
    onClickGoogleButton: () -> Unit,
    onClick: (String, String) -> Unit
) {

    var email by rememberSaveable { mutableStateOf(uiLoginState.user.email) }
    var pass by rememberSaveable { mutableStateOf(uiLoginState.user.pass) }
    val (isPasswordVisible, passwordToVisible) = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it.trim() },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.login_text_field_email)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        shape = RoundedCornerShape(15.dp),
    )

    OutlinedTextField(
        value = pass,
        onValueChange = { pass = it.trim() },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            LoginPasswordIcon(
                isPasswordVisible = isPasswordVisible,
                passwordToVisible = { passwordToVisible(!isPasswordVisible) },
            )
        },
        visualTransformation = when (isPasswordVisible) {
            false -> PasswordVisualTransformation()
            true -> VisualTransformation.None
        },
        label = { Text(text = stringResource(R.string.login_text_field_password)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(15.dp),
    )

//    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.padding(6.dp)) {
        Text(
            text = stringResource(uiLoginState.screenState.accountText),
            modifier = Modifier.padding(end = 4.dp),
        )
        Text(
            text = stringResource(uiLoginState.screenState.signText),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onChangeScreenState() }
        )
    }

    Button(
        onClick = { onClick(email, pass) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text(text = stringResource(id = uiLoginState.screenState.buttonText))
    }

    OutlinedButton(
        onClick = onClickGoogleButton,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(15.dp)
    ) {
        GoogleButtonContent(isLoading = uiLoginState.isLoading)
    }

//    Text(
//        modifier = Modifier.clickable {
//            onRecoveryPassUpdate(
//                recoverPassUIState.copy(
//                    isDialogVisible = true,
//                    emailErrorMessage = null,
//                    errorMessage = null,
//                ),
//            )
//        },
//        text = stringResource(R.string.login_text_forgot_your_password),
//        color = MaterialTheme.colorScheme.outline,
//    )
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
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = R.drawable.ic_google.toString(),
            modifier = Modifier
                .size(30.dp)
                .padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isLoading) "Signing in..." else "Sign in with Google",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}