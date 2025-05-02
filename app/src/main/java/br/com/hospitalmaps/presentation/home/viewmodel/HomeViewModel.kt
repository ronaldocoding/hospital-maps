package br.com.hospitalmaps.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hospitalmaps.data.repository.LocationRepository
import br.com.hospitalmaps.domain.model.UserLocation
import br.com.hospitalmaps.domain.usecase.GetNearestHospitalUseCase
import br.com.hospitalmaps.presentation.home.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getNearestHospitalUseCase: GetNearestHospitalUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
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

    fun getUserLocation() {
        viewModelScope.launch {
            val userLocation = locationRepository.getUserLastLocation().first()
            _uiState.update {
                it.copy(
                    userLocation = UserLocation(
                        latitude = userLocation.latitude,
                        longitude = userLocation.longitude,
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun onMapLoaded() {

    }
}