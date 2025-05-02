package br.com.hospitalmaps.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.domain.usecase.GetNearestHospitalUseCase
import br.com.hospitalmaps.presentation.main.state.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getNearestHospitalUseCase: GetNearestHospitalUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun getNearestHospital() {
        viewModelScope.launch {
            val nearestHospital = getNearestHospitalUseCase()
            _uiState.update {
                it.copy(
                    nearestHospital = nearestHospital,
                    isLoading = false
                )
            }
        }
    }
}