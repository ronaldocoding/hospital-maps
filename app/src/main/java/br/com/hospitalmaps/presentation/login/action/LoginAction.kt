package br.com.hospitalmaps.presentation.login.action

sealed class LoginAction {
    data object OnInitialized : LoginAction()
    data class OnEmailChanged(val email: String) : LoginAction()
    data class OnPasswordChanged(val password: String) : LoginAction()
    data object OnPasswordVisibilityToggled : LoginAction()
    data class OnLoginClicked(val email: String, val password: String) : LoginAction()
    data object OnForgotPasswordClicked : LoginAction()
    data object OnSignUpClicked : LoginAction()
    data object OnBackClicked : LoginAction()
}

