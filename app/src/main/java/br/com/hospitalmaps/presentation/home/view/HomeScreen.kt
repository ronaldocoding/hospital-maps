package br.com.hospitalmaps.presentation.home.view

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(onBackButtonClick: () -> Unit, onNavigate: (placeId: String) -> Unit) {
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
    onNavigate: (placeId: String) -> Unit
) {
    val context = LocalContext.current
    val userLocation = (uiState as HomeUiState.Success).uiModel.userLocationData
    val nearbyHospitals = uiState.uiModel.nearbyHospitals
    val isMapLoading = uiState.uiModel.isMapLoading
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

    var selectedMarkerIndex by remember { mutableStateOf<Int?>(null) }

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
                zoomControlsEnabled = false,
                mapToolbarEnabled = false
            )
        )
    }

    when {
        userLocation.isEmpty() -> {
            EmptyLocationState(
                onRetryLocation = {
                    viewModel.onAction(HomeAction.OnInit)
                },
                onOpenSettings = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                },
                onCloseApp = {
                    (context as? Activity)?.finish()
                }
            )
        }

        nearbyHospitals.isEmpty() && !isMapLoading -> {
            EmptyHospitalsState(
                onCloseApp = {
                    (context as? Activity)?.finish()
                }
            )
        }

        else -> {
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
                                onNavigate(nearbyHospitals[0].placeId)
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
                    onMapClick = {
                        selectedMarkerIndex = null
                    },
                    properties = mapProperties,
                    contentPadding = PaddingValues(
                        top = statusBarHeightDp(),
                        bottom = bottomBarHeightDp()
                    ),
                    uiSettings = uiSettings
                ) {
                    hospitalMarkerStates.forEachIndexed { index, markerState ->
                        if (selectedMarkerIndex == index) {
                            MarkerInfoWindow(
                                state = markerState,
                                onInfoWindowClick = {
                                    Log.d(
                                        "HomeScreen",
                                        "Hospital selected: ${nearbyHospitals[index].name}, navigating to details..."
                                    )
                                    Log.d(
                                        "HomeScreen",
                                        "Hospital coordinates: ${hospitalPoints[index]}"
                                    )
                                    onNavigate(nearbyHospitals[index].placeId)
                                }
                            ) { _ ->
                                HospitalInfoCard(
                                    hospitalName = nearbyHospitals[index].name,
                                    distance = stringResource(
                                        R.string.distance_from_user,
                                        nearbyHospitals[index].distanceFromCenter
                                    ),
                                    onNavigateClick = {
                                        onNavigate(nearbyHospitals[index].placeId)
                                    },
                                    onDismiss = {
                                        selectedMarkerIndex = null
                                    }
                                )
                            }
                        } else {
                            Marker(
                                state = markerState,
                                onClick = {
                                    selectedMarkerIndex = index
                                    true
                                }
                            )
                        }
                    }
                }
            }

            if (isMapLoading || (nearbyHospitals.isEmpty() && userLocation.isEmpty().not())) {
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
    }
}

@Composable
private fun HospitalInfoCard(
    hospitalName: String,
    distance: String,
    onNavigateClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .wrapContentHeight()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = hospitalName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location_on),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = distance,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = {
                    onNavigateClick()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_navigation),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.navigate_to_hospital),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmptyHospitalsState(onCloseApp: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.image_hospital_maps),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.no_hospitals_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_hospitals_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.no_hospitals_suggestions),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCloseApp,
            colors = ButtonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.close_app_button),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyLocationState(
    onRetryLocation: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseApp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_location_on),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.no_location_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_location_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.no_location_suggestions),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetryLocation,
                colors = ButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.retry_location_button),
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick = onOpenSettings,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.open_settings_button),
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick = onCloseApp,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.close_app_button),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun UserLocationData.isEmpty() = latitude == 0.0 && longitude == 0.0
