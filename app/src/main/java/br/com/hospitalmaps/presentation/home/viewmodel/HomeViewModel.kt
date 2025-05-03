package br.com.hospitalmaps.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.data.repository.UserLocationRepository
import br.com.hospitalmaps.data.model.UserLocationData
import br.com.hospitalmaps.data.repository.HospitalRepository
import br.com.hospitalmaps.presentation.home.action.HomeAction
import br.com.hospitalmaps.presentation.home.state.HomeUiModel
import br.com.hospitalmaps.presentation.home.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userLocationRepository: UserLocationRepository,
    private val hospitalRepository: HospitalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.LoadingUserData)
    val uiState = _uiState.asStateFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnInit -> handleOnInit()
            HomeAction.OnMapLoaded -> handleOnMapLoaded()
        }
    }

    private fun handleOnInit() {
        viewModelScope.launch {
            val userLocation = userLocationRepository.getUserLastLocation().first()
            val userLocationData = UserLocationData(
                userLocation.latitude,
                userLocation.longitude
            )
            val nearbyHospitals = hospitalRepository.getNearbyHospitals(
                userLocation
            ).first()
            _uiState.value = HomeUiState.LoadingMap(
                HomeUiModel(
                    userLocationData = userLocationData,
                    nearbyHospitals = nearbyHospitals
                )
            )
        }
    }

    private fun handleOnMapLoaded() {
        val uiModel = (_uiState.value as? HomeUiState.LoadingMap)?.uiModel ?: return
        _uiState.value = HomeUiState.Success(uiModel)
    }
}