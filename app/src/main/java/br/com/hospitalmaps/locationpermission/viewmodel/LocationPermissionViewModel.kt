package br.com.hospitalmaps.locationpermission.viewmodel

import android.text.BoringLayout
import androidx.lifecycle.ViewModel
import br.com.hospitalmaps.locationpermission.state.LocationPermissionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocationPermissionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LocationPermissionUiState())
    val uiState = _uiState.asStateFlow()

    fun onInit(isApproximateGranted: Boolean, isPreciseGranted: Boolean) {
        _uiState.update {
            it.copy(
                isApproximateGranted = isApproximateGranted,
                isPreciseGranted = isPreciseGranted
            )
        }
    }

    fun setPermissionsAsGranted() {
        _uiState.update {
            it.copy(
                isApproximateGranted = true,
                isPreciseGranted = true
            )
        }
    }

    fun setPermissionsAsDenied() {
        _uiState.update {
            it.copy(
                areAllPermissionsDenied = true
            )
        }
    }
}