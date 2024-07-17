package com.drabatx.chatio.presentation.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.drabatx.chatio.R
import com.drabatx.chatio.presentation.view.dialogs.LoadingDialog
import com.drabatx.chatio.presentation.view.dialogs.MessageDialog
import com.drabatx.chatio.presentation.view.theme.margin_big
import com.drabatx.chatio.presentation.view.theme.margin_medium
import com.drabatx.chatio.presentation.view.theme.margin_xsmall
import com.drabatx.chatio.presentation.view.widgets.TopAppBarTransparente
import com.drabatx.chatio.presentation.viewmodels.LoginViewModel
import com.drabatx.chatio.utils.Result

@Preview
@Composable
fun LoginScreenPreview() {
//    LoginScreen()
}

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavController) {
    val isValidData by loginViewModel.isValidData.collectAsState()
    val loginState by loginViewModel.loginStateFlow.collectAsState()

    Scaffold(topBar = { TopAppBarTransparente() }, content = { innerPadding ->
        when (loginState) {
            is Result.Loading -> {
                LoadingDialog(true)
            }

            is Result.Error -> {
                val message = ((loginState as Result.Error).exception.message)
                    ?: stringResource(R.string.error_login)
                MessageDialog(
                    title = stringResource(id = R.string.error),
                    text = message,
                    showDialog = true,
                    onConfirm = { loginViewModel.resetForm() },
                    secondaryButtonText = stringResource(
                        id = R.string.accept
                    )
                )
            }

            is Result.Success -> {
                MessageDialog(
                    title = stringResource(id = R.string.success), text = stringResource(
                        id = R.string.success
                    ), showDialog = true
                )
            }

            else -> {}
        }
        LoginView(innerPadding, loginViewModel, isValidData)
    })
}

@Composable
private fun LoginView(
    innerPadding: PaddingValues,
    loginViewModel: LoginViewModel,
    isValidData: LoginViewModel.LOGINSTATE
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .imePadding()
    ) {
        val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
        val (background, logo, form) = createRefs()
        Background(modifier = Modifier.constrainAs(background) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        if (imeBottom == 0) {
            LogoView(modifier = Modifier.constrainAs(logo) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(form.top)
                height = Dimension.fillToConstraints
            })
        }
        FormularioLogin(
            modifier = Modifier.constrainAs(form) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            onLoginClick = { email, password ->
                loginViewModel.isValidData(email, password)
            },
            onRegisterClick = {
            }
        )
        when (isValidData) {
            LoginViewModel.LOGINSTATE.ERROR_EMAIL -> {
                MessageDialog(
                    title = stringResource(id = R.string.title_erro_email_incorrect),
                    text = stringResource(
                        id = R.string.error_email_incorrect
                    ),
                    primaryButtonText = stringResource(id = R.string.accept),
                    onConfirm = { loginViewModel.resetForm() },
                    showDialog = true
                )
            }

            LoginViewModel.LOGINSTATE.ERROR_PASSWORD -> {
                MessageDialog(
                    title = stringResource(id = R.string.title_erro_password_incorrect),
                    text = stringResource(
                        id = R.string.error_password_incorrect
                    ),
                    primaryButtonText = stringResource(id = R.string.accept),
                    onConfirm = { loginViewModel.resetForm() },
                    showDialog = true
                )
            }

            else -> {}
        }
    }
}


@Composable
fun Background(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.background_hex),
        contentDescription = "Descripción de la imagen",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun FormularioLogin(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = margin_medium,
                top = margin_big,
                end = margin_medium
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.label_welcome),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    stringResource(R.string.email),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
        )

        Spacer(modifier = Modifier.height(height = margin_medium))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    stringResource(R.string.password),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onLoginClick(email, password)
                }
            ),
        )
        Spacer(modifier = Modifier.height(height = margin_medium))
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(
                    1f
                )
            )
        ) {
            Text(stringResource(R.string.label_login))
        }
        Spacer(modifier = Modifier.height(height = margin_medium))
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(1f))
        ) {
            Text(
                stringResource(R.string.action_sign_in),
                color = MaterialTheme.colorScheme.primary.copy(1f)
            )
        }
    }
}

@Composable
fun LogoView(modifier: Modifier) {
    Column(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.logo_chat_colors),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .padding(top = margin_medium)
                .weight(1f)
        )
        Spacer(modifier = Modifier.height(height = margin_xsmall))
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)

        )
    }
}