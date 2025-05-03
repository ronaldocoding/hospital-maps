package br.com.hospitalmaps.presentation.home.state

import br.com.hospitalmaps.data.model.HospitalData
import br.com.hospitalmaps.data.model.UserLocationData

data class HomeUiModel(
    val userLocationData: UserLocationData,
    val nearbyHospitals: List<HospitalData>
)

sealed class HomeUiState {
    data object LoadingUserData : HomeUiState()

    data class LoadingMap(val uiModel: HomeUiModel) : HomeUiState()

    data class Success(val userDataUiModel: HomeUiModel) : HomeUiState()
}