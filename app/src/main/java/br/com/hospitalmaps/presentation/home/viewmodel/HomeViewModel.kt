package br.com.hospitalmaps.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.data.repository.UserLocationRepository
import br.com.hospitalmaps.data.model.UserLocationData
import br.com.hospitalmaps.data.repository.HospitalRepository
import br.com.hospitalmaps.presentation.home.action.HomeAction
import br.com.hospitalmaps.presentation.home.event.HomeEvent
import br.com.hospitalmaps.presentation.home.state.HomeUiModel
import br.com.hospitalmaps.presentation.home.state.HomeUiState
import com.google.android.gms.maps.model.LatLng
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
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<HomeEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnInitialized -> handleOnInit()
            HomeAction.OnMapLoaded -> handleOnMapLoaded()
            HomeAction.OnTryAgainClicked -> handleOnInit()
            is HomeAction.OnZoomInUserClicked -> handleOnZoomToUserClicked(action.userPoint)
            HomeAction.OnZoomInMapClicked -> handleOnZoomInMapClicked()
            HomeAction.OnZoomOutMapClicked -> handleOnZoomOutMapClicked()
            HomeAction.OnPersonalInfoClicked -> handleOnPersonalInfoClicked()
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

    private fun handleOnZoomToUserClicked(userPoint: LatLng) {
        viewModelScope.launch {
            _event.send(HomeEvent.ZoomInUser(userPoint))
        }
    }

    private fun handleOnZoomInMapClicked() {
        viewModelScope.launch {
            _event.send(HomeEvent.ZoomInMap)
        }
    }

    private fun handleOnZoomOutMapClicked() {
        viewModelScope.launch {
            _event.send(HomeEvent.ZoomOutMap)
        }
    }

    private fun handleOnPersonalInfoClicked() {
        viewModelScope.launch {
            _event.send(HomeEvent.NavigateToPersonalInfo)
        }
    }
}
