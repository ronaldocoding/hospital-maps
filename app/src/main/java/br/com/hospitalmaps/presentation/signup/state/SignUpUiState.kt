package br.com.hospitalmaps.presentation.signup.state

data class SignUpUiModel(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
)

sealed class SignUpUiState {
    data object Idle : SignUpUiState()

    data class Content(val uiModel: SignUpUiModel) : SignUpUiState()

    data object Error : SignUpUiState()
}

