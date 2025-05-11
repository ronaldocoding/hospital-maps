package br.com.hospitalmaps.presentation.home.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.R
import br.com.hospitalmaps.presentation.home.action.HomeAction
import br.com.hospitalmaps.presentation.home.state.HomeUiState
import br.com.hospitalmaps.presentation.home.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(onBackButtonClick: () -> Unit) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(HomeAction.OnInit)
    }

    BackHandler { onBackButtonClick.invoke() }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .systemBarsPadding(),
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
                HomeContent(uiState, viewModel)
            }

            is HomeUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.home_error_title))
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
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val userLocation = (uiState as HomeUiState.Success).uiModel.userLocationData
    val nearbyHospitals = uiState.uiModel.nearbyHospitals
    val userPoint = LatLng(userLocation.latitude, userLocation.longitude)
    val hospitalPoints = nearbyHospitals.map {
        LatLng(it.latitude, it.longitude)
    }

    val userMarkerState = rememberMarkerState(position = userPoint)
    val hospitalMarkerStates = hospitalPoints.map { point ->
        rememberMarkerState(position = point)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userPoint, 12f)
    }
    val isDarkTheme = isSystemInDarkTheme()
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style_dark
                ) else null
            )
        )
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { viewModel.onAction(HomeAction.OnMapLoaded) },
        properties = mapProperties
    ) {
        MarkerComposable(
            state = userMarkerState,
            title = "User Location",
            snippet = "Marker in User Location",
        ) {
            Box {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.image_user_pin),
                    contentDescription = null
                )
            }
        }
        hospitalMarkerStates.forEachIndexed { index, markerState ->
            Marker(
                state = markerState,
                title = nearbyHospitals[index].name,
                snippet = "Distância do usuário: ${nearbyHospitals[index].distanceFromCenter} KM",
            )
        }
    }
    if (uiState.uiModel.isMapLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}