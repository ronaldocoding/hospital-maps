package br.com.hospitalmaps.presentation.home.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.presentation.home.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.getUserLocation()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            else -> {
                val userLocation = LatLng(
                    uiState.userLocation.latitude,
                    uiState.userLocation.longitude
                )
                val userMarkerState = rememberMarkerState(position = userLocation)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(userLocation, 18f)
                }
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeContentPadding(),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = {
                        viewModel.onMapLoaded()
                    }
                ) {
                    Marker(
                        state = userMarkerState,
                        title = "User Location",
                        snippet = "Marker in User Location"
                    )
                }
            }
        }
    }
}