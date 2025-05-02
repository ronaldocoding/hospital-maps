package br.com.hospitalmaps.presentation.main.state

import br.com.hospitalmaps.domain.model.HospitalData

data class MainUiState(
    val nearestHospital: HospitalData = HospitalData("", 0.0, 0.0),
    val isLoading: Boolean = true
)