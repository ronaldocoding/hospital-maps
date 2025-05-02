package br.com.hospitalmaps.presentation.locationpermission.state

sealed class LocationPermissionUiState {
    data object Loading : LocationPermissionUiState()
    data object RequestPermission : LocationPermissionUiState()
    data object Denied : LocationPermissionUiState()
    data object Granted : LocationPermissionUiState()
    data object Paused : LocationPermissionUiState()
}