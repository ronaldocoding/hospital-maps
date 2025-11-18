package br.com.hospitalmaps.presentation.signup.event

sealed interface SignUpEvent {
    data object NavigateToHome : SignUpEvent
    data object NavigateToLogin : SignUpEvent
    data object NavigateBack : SignUpEvent
    data class ShowError(val message: String) : SignUpEvent
}

