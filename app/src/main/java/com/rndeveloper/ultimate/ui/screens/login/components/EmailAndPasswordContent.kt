package com.rndeveloper.ultimate.ui.screens.login.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    loginUiState: LoginUiState,
    onChange: () -> Unit,
    onClick: (String, String) -> Unit
) {

    var email by rememberSaveable { mutableStateOf(loginUiState.user.email) }
    var pass by rememberSaveable { mutableStateOf(loginUiState.user.pass) }
    val (isPasswordVisible, passwordToVisible) = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it.trim() },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.login_text_field_email)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        shape = RoundedCornerShape(25.dp),
        isError = !loginUiState.emailErrorMessage?.error.isNullOrBlank(),
        supportingText = {
            SupportingErrorText(loginUiState.emailErrorMessage?.error)
        },
    )

//    Spacer(modifier = Modifier.height(4.dp))

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
        shape = RoundedCornerShape(25.dp),
        isError = !loginUiState.passErrorMessage?.error.isNullOrBlank(),
        supportingText = {
            SupportingErrorText(loginUiState.passErrorMessage?.error)
        },
    )

//    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier
            .padding(6.dp)
            .clickable { onChange() },
    ) {
        Text(
            text = stringResource(loginUiState.screenState.accountText),
            modifier = Modifier.padding(end = 4.dp),
        )

        // TODO: Create Typography for this text.
        Text(
            text = stringResource(loginUiState.screenState.signText),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold,
        )
    }

    Button(
        onClick = { onClick(email, pass) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = loginUiState.screenState.buttonText),
            modifier = Modifier.padding(4.dp),
        )
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
fun SupportingErrorText(errorMessage: String?) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        errorMessage?.let {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = "Error",
                modifier = Modifier.padding(4.dp),
            )
            Text(text = it)
        }
    }
}