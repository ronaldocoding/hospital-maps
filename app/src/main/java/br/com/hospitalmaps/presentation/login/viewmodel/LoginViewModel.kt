package br.com.hospitalmaps.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.presentation.login.action.LoginAction
import br.com.hospitalmaps.presentation.login.event.LoginEvent
import br.com.hospitalmaps.presentation.login.state.LoginUiModel
import br.com.hospitalmaps.presentation.login.state.LoginUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<LoginEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnInitialized -> handleOnInitialized()
            is LoginAction.OnEmailChanged -> handleOnEmailChanged(action.email)
            is LoginAction.OnPasswordChanged -> handleOnPasswordChanged(action.password)
            LoginAction.OnPasswordVisibilityToggled -> handleOnPasswordVisibilityToggled()
            is LoginAction.OnLoginClicked -> handleOnLoginClicked(action.email, action.password)
            LoginAction.OnForgotPasswordClicked -> handleOnForgotPasswordClicked()
            LoginAction.OnSignUpClicked -> handleOnSignUpClicked()
            LoginAction.OnBackClicked -> handleOnBackClicked()
        }
    }

    private fun handleOnInitialized() {
        _uiState.value = LoginUiState.Content(LoginUiModel())
    }

    private fun handleOnEmailChanged(email: String) {
        val currentState = (_uiState.value as? LoginUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = LoginUiState.Content(currentState.copy(email = email))
        }
    }

    private fun handleOnPasswordChanged(password: String) {
        val currentState = (_uiState.value as? LoginUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = LoginUiState.Content(currentState.copy(password = password))
        }
    }

    private fun handleOnPasswordVisibilityToggled() {
        val currentState = (_uiState.value as? LoginUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = LoginUiState.Content(
                currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
            )
        }
    }

    private fun handleOnLoginClicked(email: String, password: String) {
        viewModelScope.launch {
            // TODO: Implement login authentication logic
            // This is where you would call your authentication repository/use case
            // For now, we'll just emit a navigation event
            _event.send(LoginEvent.NavigateToHome)
        }
    }

    private fun handleOnForgotPasswordClicked() {
        viewModelScope.launch {
            _event.send(LoginEvent.NavigateToForgotPassword)
        }
    }

    private fun handleOnSignUpClicked() {
        viewModelScope.launch {
            _event.send(LoginEvent.NavigateToSignUp)
        }
    }

    private fun handleOnBackClicked() {
        viewModelScope.launch {
            _event.send(LoginEvent.NavigateBack)
        }
    }
}

