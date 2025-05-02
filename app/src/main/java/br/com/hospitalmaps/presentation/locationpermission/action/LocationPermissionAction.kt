package br.com.hospitalmaps.presentation.locationpermission.action

sealed class LocationPermissionAction {
    data class OnInit(
        val isApproximateGranted: Boolean,
        val isPreciseGranted: Boolean
    ) : LocationPermissionAction()

    data object OnPermissionsDenied : LocationPermissionAction()

    data object OnPause : LocationPermissionAction()

    data object OnPermissionsGranted : LocationPermissionAction()
}