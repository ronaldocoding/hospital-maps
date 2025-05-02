package br.com.hospitalmaps.presentation.locationpermission.viewmodel

import androidx.lifecycle.ViewModel
import br.com.hospitalmaps.presentation.locationpermission.action.LocationPermissionAction
import br.com.hospitalmaps.presentation.locationpermission.state.LocationPermissionUiState
import br.com.hospitalmaps.presentation.locationpermission.state.LocationPermissionUiState.Denied
import br.com.hospitalmaps.presentation.locationpermission.state.LocationPermissionUiState.Granted
import br.com.hospitalmaps.presentation.locationpermission.state.LocationPermissionUiState.Loading
import br.com.hospitalmaps.presentation.locationpermission.state.LocationPermissionUiState.Paused
import br.com.hospitalmaps.presentation.locationpermission.state.LocationPermissionUiState.RequestPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationPermissionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LocationPermissionUiState>(Loading)
    val uiState = _uiState.asStateFlow()

    fun onAction(action: LocationPermissionAction) {
        when (action) {
            is LocationPermissionAction.OnInit -> handleOnInit(
                action.isApproximateGranted,
                action.isPreciseGranted
            )

            LocationPermissionAction.OnPermissionsDenied -> _uiState.value = Denied

            LocationPermissionAction.OnPause -> _uiState.value = Paused

            LocationPermissionAction.OnPermissionsGranted -> _uiState.value = Granted
        }
    }

    private fun handleOnInit(isApproximateGranted: Boolean, isPreciseGranted: Boolean) {
        if (isApproximateGranted && isPreciseGranted) {
            _uiState.value = Granted
        } else {
            _uiState.value = RequestPermission
        }
    }
}