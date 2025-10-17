package br.com.hospitalmaps.presentation.home.state

import br.com.hospitalmaps.data.model.HospitalData
import br.com.hospitalmaps.data.model.UserLocationData

data class HomeUiModel(
    val userLocationData: UserLocationData,
    val nearbyHospitals: List<HospitalData>,
    val isMapLoading: Boolean = true,
)

sealed class HomeUiState {
    data object LoadingUserData : HomeUiState()

    data class Success(val uiModel: HomeUiModel) : HomeUiState()

    data object Error : HomeUiState()
}