package br.com.hospitalmaps.presentation.home.state

import br.com.hospitalmaps.data.model.HospitalData
import br.com.hospitalmaps.data.model.UserLocation

data class HomeUiState(
    val nearestHospital: HospitalData = HospitalData("", 0.0, 0.0,  0f),
    val userLocation: UserLocation = UserLocation(0.0, 0.0),
    val isLoading: Boolean = true
)