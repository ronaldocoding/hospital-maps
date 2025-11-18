package br.com.hospitalmaps.presentation.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.presentation.signup.action.SignUpAction
import br.com.hospitalmaps.presentation.signup.event.SignUpEvent
import br.com.hospitalmaps.presentation.signup.state.SignUpUiModel
import br.com.hospitalmaps.presentation.signup.state.SignUpUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<SignUpEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            SignUpAction.OnInitialized -> handleOnInitialized()
            is SignUpAction.OnEmailChanged -> handleOnEmailChanged(action.email)
            is SignUpAction.OnPasswordChanged -> handleOnPasswordChanged(action.password)
            is SignUpAction.OnConfirmPasswordChanged -> handleOnConfirmPasswordChanged(action.confirmPassword)
            SignUpAction.OnPasswordVisibilityToggled -> handleOnPasswordVisibilityToggled()
            SignUpAction.OnConfirmPasswordVisibilityToggled -> handleOnConfirmPasswordVisibilityToggled()
            is SignUpAction.OnSignUpClicked -> handleOnSignUpClicked(
                action.email,
                action.password,
                action.confirmPassword
            )
            SignUpAction.OnLoginClicked -> handleOnLoginClicked()
            SignUpAction.OnBackClicked -> handleOnBackClicked()
        }
    }

    private fun handleOnInitialized() {
        _uiState.value = SignUpUiState.Content(SignUpUiModel())
    }

    private fun handleOnEmailChanged(email: String) {
        val currentState = (_uiState.value as? SignUpUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = SignUpUiState.Content(currentState.copy(email = email))
        }
    }

    private fun handleOnPasswordChanged(password: String) {
        val currentState = (_uiState.value as? SignUpUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = SignUpUiState.Content(currentState.copy(password = password))
        }
    }

    private fun handleOnConfirmPasswordChanged(confirmPassword: String) {
        val currentState = (_uiState.value as? SignUpUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = SignUpUiState.Content(currentState.copy(confirmPassword = confirmPassword))
        }
    }

    private fun handleOnPasswordVisibilityToggled() {
        val currentState = (_uiState.value as? SignUpUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = SignUpUiState.Content(
                currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
            )
        }
    }

    private fun handleOnConfirmPasswordVisibilityToggled() {
        val currentState = (_uiState.value as? SignUpUiState.Content)?.uiModel
        if (currentState != null) {
            _uiState.value = SignUpUiState.Content(
                currentState.copy(isConfirmPasswordVisible = !currentState.isConfirmPasswordVisible)
            )
        }
    }

    private fun handleOnSignUpClicked(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Validate passwords match
            if (password != confirmPassword) {
                _event.send(SignUpEvent.ShowError("Passwords do not match"))
                return@launch
            }

            // TODO: Implement sign up authentication logic
            // This is where you would call your authentication repository/use case
            // For now, we'll just emit a navigation event to home
            _event.send(SignUpEvent.NavigateToHome)
        }
    }

    private fun handleOnLoginClicked() {
        viewModelScope.launch {
            _event.send(SignUpEvent.NavigateToLogin)
        }
    }

    private fun handleOnBackClicked() {
        viewModelScope.launch {
            _event.send(SignUpEvent.NavigateBack)
        }
    }
}

