package br.com.hospitalmaps.locationpermission.state

data class LocationPermissionUiState(
    val isApproximateGranted: Boolean = false,
    val isPreciseGranted: Boolean = false,
    val areAllPermissionsDenied: Boolean = false
)