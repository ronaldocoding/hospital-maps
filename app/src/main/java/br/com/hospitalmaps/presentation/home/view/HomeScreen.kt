package br.com.hospitalmaps.presentation.home.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.R
import br.com.hospitalmaps.data.model.UserLocationData
import br.com.hospitalmaps.presentation.home.action.HomeAction
import br.com.hospitalmaps.presentation.home.state.HomeUiState
import br.com.hospitalmaps.presentation.home.viewmodel.HomeViewModel
import br.com.hospitalmaps.shared.bottomBarHeightDp
import br.com.hospitalmaps.shared.statusBarHeightDp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(onBackButtonClick: () -> Unit, onNavigate: (LatLng) -> Unit) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(HomeAction.OnInit)
    }

    BackHandler { onBackButtonClick.invoke() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is HomeUiState.LoadingUserData -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimary
                )
            }

            is HomeUiState.Success -> {
                HomeContent(uiState, viewModel, onNavigate)
            }

            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.home_error_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.onAction(HomeAction.TryAgain) },
                        colors = ButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(text = stringResource(R.string.home_error_button_label))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onNavigate: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val userLocation = (uiState as HomeUiState.Success).uiModel.userLocationData
    val nearbyHospitals = uiState.uiModel.nearbyHospitals
    val userPoint = LatLng(userLocation.latitude, userLocation.longitude)
    val hospitalPoints = nearbyHospitals.map {
        LatLng(it.latitude, it.longitude)
    }
    val hospitalMarkerStates = hospitalPoints.map { point ->
        rememberMarkerState(position = point)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userPoint, 10f)
    }
    val isDarkTheme = isSystemInDarkTheme()
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style_dark
                ) else null,
                isMyLocationEnabled = true
            )
        )
    }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false
            )
        )
    }
    if (nearbyHospitals.isEmpty().not() && userLocation.isEmpty().not()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                Box(
                    modifier = Modifier.padding(bottom = bottomBarHeightDp())
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        onClick = {
                            onNavigate(hospitalPoints[0])
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.image_hospital_maps),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(32.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (uiState.uiModel.isMapLoading) 0f else 1f)
                    .padding(innerPadding),
                cameraPositionState = cameraPositionState,
                onMapLoaded = { viewModel.onAction(HomeAction.OnMapLoaded) },
                properties = mapProperties,
                contentPadding = PaddingValues(
                    top = statusBarHeightDp(),
                    bottom = bottomBarHeightDp()
                ),
                uiSettings = uiSettings
            ) {
                hospitalMarkerStates.forEachIndexed { index, markerState ->
                    Marker(
                        state = markerState,
                        title = nearbyHospitals[index].name,
                        snippet = stringResource(
                            R.string.distance_from_user,
                            nearbyHospitals[index].distanceFromCenter
                        ),
                        onInfoWindowClick = {
                            Log.d(
                                "HomeScreen",
                                "Hospital selected: ${nearbyHospitals[index].name}, navigating to details..."
                            )
                            Log.d("HomeScreen", "Hospital coordinates: ${hospitalPoints[index]}")
                            onNavigate(hospitalPoints[index])
                        }
                    )
                }
            }
        }
    }
    if (uiState.uiModel.isMapLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

fun UserLocationData.isEmpty() = latitude == 0.0 && longitude == 0.0
