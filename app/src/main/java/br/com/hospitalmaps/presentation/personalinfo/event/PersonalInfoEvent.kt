package br.com.hospitalmaps.presentation.personalinfo.event

sealed interface PersonalInfoEvent {
    data object NavigateBack : PersonalInfoEvent
    data object NavigateToEditMedicines : PersonalInfoEvent
    data object NavigateToEditAllergies : PersonalInfoEvent
    data object NavigateToEditDiseases : PersonalInfoEvent
    data class ShowError(val message: String) : PersonalInfoEvent
}

