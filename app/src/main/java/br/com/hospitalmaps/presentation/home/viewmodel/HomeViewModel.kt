package br.com.hospitalmaps.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.data.repository.UserLocationRepository
import br.com.hospitalmaps.data.model.UserLocationData
import br.com.hospitalmaps.data.repository.HospitalRepository
import br.com.hospitalmaps.presentation.home.action.HomeAction
import br.com.hospitalmaps.presentation.home.effect.HomeUiEffect
import br.com.hospitalmaps.presentation.home.state.HomeUiModel
import br.com.hospitalmaps.presentation.home.state.HomeUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userLocationRepository: UserLocationRepository,
    private val hospitalRepository: HospitalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.LoadingUserData)

    private val _uiEffect = Channel<HomeUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    val uiState = _uiState.asStateFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnInit -> handleOnInit()
            HomeAction.OnMapLoaded -> handleOnMapLoaded()
            HomeAction.TryAgain -> handleOnInit()
            HomeAction.OnZoomToUserClicked -> handleOnZoomToUserClicked()
        }
    }

    private fun handleOnInit() {
        viewModelScope.launch {
            try {
                val userLocation = userLocationRepository.getUserLastLocation().first()
                val userLocationData = UserLocationData(
                    userLocation.latitude,
                    userLocation.longitude
                )
                hospitalRepository.getNearbyHospitals(
                    centerLocation = userLocation,
                    onSuccess = {
                        _uiState.value = HomeUiState.Success(
                            HomeUiModel(
                                userLocationData = userLocationData,
                                nearbyHospitals = it
                            )
                        )
                    },
                    onFailure = {
                        _uiState.value = HomeUiState.Error
                    }
                )
            } catch (_: Exception) {
                _uiState.value = HomeUiState.Error
            }
        }
    }

    private fun handleOnMapLoaded() {
        val currentUiModel = (_uiState.value as? HomeUiState.Success)?.uiModel
        with(checkNotNull(currentUiModel)) {
            _uiState.value = HomeUiState.Success(this@with.copy(isMapLoading = false))
        }
    }

    private fun handleOnZoomToUserClicked() {
        viewModelScope.launch {
            _uiEffect.send(HomeUiEffect.ZoomToUser)
        }
    }
}