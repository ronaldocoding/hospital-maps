package br.com.hospitalmaps.presentation.login.state

data class LoginUiModel(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
)

sealed class LoginUiState {
    data object Idle : LoginUiState()

    data class Content(val uiModel: LoginUiModel) : LoginUiState()

    data object Error : LoginUiState()
}

