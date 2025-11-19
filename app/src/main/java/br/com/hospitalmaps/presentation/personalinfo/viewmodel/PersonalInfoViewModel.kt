package br.com.hospitalmaps.presentation.personalinfo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.presentation.personalinfo.action.PersonalInfoAction
import br.com.hospitalmaps.presentation.personalinfo.event.PersonalInfoEvent
import br.com.hospitalmaps.presentation.personalinfo.state.PersonalInfoUiState
import br.com.hospitalmaps.presentation.personalinfo.state.getFakePersonalInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PersonalInfoUiState>(PersonalInfoUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<PersonalInfoEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: PersonalInfoAction) {
        when (action) {
            PersonalInfoAction.OnInitialized -> handleOnInitialized()
            PersonalInfoAction.OnEditMedicinesClicked -> handleOnEditMedicinesClicked()
            PersonalInfoAction.OnEditAllergiesClicked -> handleOnEditAllergiesClicked()
            PersonalInfoAction.OnEditDiseasesClicked -> handleOnEditDiseasesClicked()
            PersonalInfoAction.OnBackClicked -> handleOnBackClicked()
        }
    }

    private fun handleOnInitialized() {
        // Load fake data for now
        _uiState.value = PersonalInfoUiState.Content(getFakePersonalInfo())
    }

    private fun handleOnEditMedicinesClicked() {
        viewModelScope.launch {
            _event.send(PersonalInfoEvent.NavigateToEditMedicines)
        }
    }

    private fun handleOnEditAllergiesClicked() {
        viewModelScope.launch {
            _event.send(PersonalInfoEvent.NavigateToEditAllergies)
        }
    }

    private fun handleOnEditDiseasesClicked() {
        viewModelScope.launch {
            _event.send(PersonalInfoEvent.NavigateToEditDiseases)
        }
    }

    private fun handleOnBackClicked() {
        viewModelScope.launch {
            _event.send(PersonalInfoEvent.NavigateBack)
        }
    }
}

