package br.com.hospitalmaps.presentation.login.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.R
import br.com.hospitalmaps.presentation.login.action.LoginAction
import br.com.hospitalmaps.presentation.login.event.LoginEvent
import br.com.hospitalmaps.presentation.login.state.LoginUiState
import br.com.hospitalmaps.presentation.login.viewmodel.LoginViewModel
import br.com.hospitalmaps.shared.utils.ObserveAsEvents
import br.com.hospitalmaps.shared.utils.VisibilityIcon
import br.com.hospitalmaps.shared.utils.VisibilityOffIcon
import br.com.hospitalmaps.ui.theme.HospitalMapsAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val viewModel: LoginViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isInitialized = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isInitialized.value.not()) viewModel.onAction(LoginAction.OnInitialized)
        isInitialized.value = true
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            LoginEvent.NavigateToHome -> onLoginClick("", "")
            LoginEvent.NavigateToForgotPassword -> onForgotPasswordClick()
            LoginEvent.NavigateToSignUp -> onSignUpClick()
            LoginEvent.NavigateBack -> onBackClick()
            LoginEvent.NavigateToSignUpFlow -> onSignUpClick()
            is LoginEvent.ShowError -> {
                // TODO: Show error message to user
            }
        }
    }

    when (uiState) {
        is LoginUiState.Idle -> Unit
        is LoginUiState.Content -> {
            val model = (uiState as LoginUiState.Content).uiModel
            LoginScreenContent(
                uiModel = model,
                onEmailChanged = { viewModel.onAction(LoginAction.OnEmailChanged(it)) },
                onPasswordChanged = { viewModel.onAction(LoginAction.OnPasswordChanged(it)) },
                onPasswordVisibilityToggled = { viewModel.onAction(LoginAction.OnPasswordVisibilityToggled) },
                onLoginClick = { viewModel.onAction(LoginAction.OnLoginClicked(model.email, model.password)) },
                onForgotPasswordClick = { viewModel.onAction(LoginAction.OnForgotPasswordClicked) },
                onSignUpClick = { viewModel.onAction(LoginAction.OnSignUpClicked) },
                onBackClick = { viewModel.onAction(LoginAction.OnBackClicked) }
            )
        }

        is LoginUiState.Error -> Unit
    }
}

@Composable
private fun LoginScreenContent(
    uiModel: br.com.hospitalmaps.presentation.login.state.LoginUiModel,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Subtitle
        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Email Field
        OutlinedTextField(
            value = uiModel.email,
            onValueChange = onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(R.string.login_email_label),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = stringResource(R.string.login_email_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = uiModel.password,
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(R.string.login_password_label),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.login_password_icon_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onPasswordVisibilityToggled
                ) {
                    Icon(
                        imageVector = if (uiModel.isPasswordVisible) {
                            VisibilityOffIcon
                        } else {
                            VisibilityIcon
                        },
                        contentDescription = stringResource(R.string.login_password_visibility_toggle),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (uiModel.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onLoginClick()
                }
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Forgot Password Link
        Text(
            text = stringResource(R.string.login_forgot_password),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            enabled = uiModel.email.isNotEmpty() && uiModel.password.isNotEmpty(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.login_button),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Link
        Text(
            text = stringResource(R.string.login_signup_link),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable(enabled = true) {
                    onSignUpClick()
                }
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun LoginScreenPreview() {
    HospitalMapsAppTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true, device = "id:pixel_5", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    HospitalMapsAppTheme(darkTheme = true) {
        LoginScreen()
    }
}
