package br.com.hospitalmaps.presentation.login.event

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToSignUp : LoginEvent
    data object NavigateToForgotPassword : LoginEvent
    data object NavigateBack : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}

