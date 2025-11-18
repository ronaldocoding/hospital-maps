package br.com.hospitalmaps.presentation.signup

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.R
import br.com.hospitalmaps.presentation.signup.action.SignUpAction
import br.com.hospitalmaps.presentation.signup.event.SignUpEvent
import br.com.hospitalmaps.presentation.signup.state.SignUpUiState
import br.com.hospitalmaps.presentation.signup.viewmodel.SignUpViewModel
import br.com.hospitalmaps.shared.utils.ObserveAsEvents
import br.com.hospitalmaps.shared.utils.VisibilityIcon
import br.com.hospitalmaps.shared.utils.VisibilityOffIcon
import br.com.hospitalmaps.ui.theme.HospitalMapsAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    onSignUpClick: (email: String, password: String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val viewModel: SignUpViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isInitialized = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isInitialized.value.not()) viewModel.onAction(SignUpAction.OnInitialized)
        isInitialized.value = true
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            SignUpEvent.NavigateToHome -> onSignUpClick("", "")
            SignUpEvent.NavigateToLogin -> onLoginClick()
            SignUpEvent.NavigateBack -> onBackClick()
            is SignUpEvent.ShowError -> {
                // TODO: Show error message to user
            }
        }
    }

    when (uiState) {
        is SignUpUiState.Idle -> Unit
        is SignUpUiState.Content -> {
            val model = (uiState as SignUpUiState.Content).uiModel
            SignUpScreenContent(
                uiModel = model,
                onEmailChanged = { viewModel.onAction(SignUpAction.OnEmailChanged(it)) },
                onPasswordChanged = { viewModel.onAction(SignUpAction.OnPasswordChanged(it)) },
                onConfirmPasswordChanged = { viewModel.onAction(SignUpAction.OnConfirmPasswordChanged(it)) },
                onPasswordVisibilityToggled = { viewModel.onAction(SignUpAction.OnPasswordVisibilityToggled) },
                onConfirmPasswordVisibilityToggled = { viewModel.onAction(SignUpAction.OnConfirmPasswordVisibilityToggled) },
                onSignUpClick = { viewModel.onAction(SignUpAction.OnSignUpClicked(model.email, model.password, model.confirmPassword)) },
                onLoginClick = { viewModel.onAction(SignUpAction.OnLoginClicked) },
                onBackClick = { viewModel.onAction(SignUpAction.OnBackClicked) }
            )
        }

        is SignUpUiState.Error -> Unit
    }
}

@Composable
private fun SignUpScreenContent(
    uiModel: br.com.hospitalmaps.presentation.signup.state.SignUpUiModel,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit,
    onConfirmPasswordVisibilityToggled: () -> Unit,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
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
            text = stringResource(R.string.signup_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Subtitle
        Text(
            text = stringResource(R.string.signup_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Email Field
        OutlinedTextField(
            value = uiModel.email,
            onValueChange = onEmailChanged,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(R.string.signup_email_label),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = stringResource(R.string.signup_email_content_description),
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
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(R.string.signup_password_label),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.signup_password_icon_content_description),
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
                        contentDescription = stringResource(R.string.signup_password_visibility_toggle),
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

        // Confirm Password Field
        OutlinedTextField(
            value = uiModel.confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(R.string.signup_confirm_password_label),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.signup_password_icon_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onConfirmPasswordVisibilityToggled
                ) {
                    Icon(
                        imageVector = if (uiModel.isConfirmPasswordVisible) {
                            VisibilityOffIcon
                        } else {
                            VisibilityIcon
                        },
                        contentDescription = stringResource(R.string.signup_password_visibility_toggle),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (uiModel.isConfirmPasswordVisible) {
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
                    onSignUpClick()
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

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Up Button
        Button(
            onClick = onSignUpClick,
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
            enabled = uiModel.email.isNotEmpty() && 
                     uiModel.password.isNotEmpty() && 
                     uiModel.confirmPassword.isNotEmpty() &&
                     uiModel.password == uiModel.confirmPassword,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.signup_button),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )
        }

        // Password Match Error
        if (uiModel.password.isNotEmpty() && 
            uiModel.confirmPassword.isNotEmpty() && 
            uiModel.password != uiModel.confirmPassword) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.signup_password_mismatch_error),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Link
        Text(
            text = stringResource(R.string.signup_login_link),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun SignUpScreenPreview() {
    HospitalMapsAppTheme {
        SignUpScreen()
    }
}

@Preview(showBackground = true, device = "id:pixel_5", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignUpScreenDarkPreview() {
    HospitalMapsAppTheme(darkTheme = true) {
        SignUpScreen()
    }
}