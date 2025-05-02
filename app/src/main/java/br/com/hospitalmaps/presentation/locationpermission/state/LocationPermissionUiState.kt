package br.com.hospitalmaps.presentation.locationpermission.state

data class LocationPermissionUiState(
    val isApproximateGranted: Boolean = false,
    val isPreciseGranted: Boolean = false,
    val areAllPermissionsDenied: Boolean = false
)