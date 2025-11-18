package br.com.hospitalmaps.presentation.signup.action

sealed class SignUpAction {
    data object OnInitialized : SignUpAction()
    data class OnEmailChanged(val email: String) : SignUpAction()
    data class OnPasswordChanged(val password: String) : SignUpAction()
    data class OnConfirmPasswordChanged(val confirmPassword: String) : SignUpAction()
    data object OnPasswordVisibilityToggled : SignUpAction()
    data object OnConfirmPasswordVisibilityToggled : SignUpAction()
    data class OnSignUpClicked(
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : SignUpAction()
    data object OnLoginClicked : SignUpAction()
    data object OnBackClicked : SignUpAction()
}

