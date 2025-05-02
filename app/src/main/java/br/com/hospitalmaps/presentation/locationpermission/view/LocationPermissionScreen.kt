package br.com.hospitalmaps.presentation.locationpermission.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.R
import br.com.hospitalmaps.presentation.locationpermission.action.LocationPermissionAction
import br.com.hospitalmaps.presentation.locationpermission.state.LocationPermissionUiState
import br.com.hospitalmaps.presentation.locationpermission.viewmodel.LocationPermissionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionScreen(onLocationPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val viewModel: LocationPermissionViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(
            LocationPermissionAction.OnInit(
                isApproximateGranted = hasApproximateLocationPermission(context),
                isPreciseGranted = hasPreciseLocationPermission(context)
            )
        )
    }

    LifecycleResumeEffect(Unit) {
        if (hasLocationPermissions(context)) {
            onLocationPermissionGranted.invoke()
        } else if (uiState.value is LocationPermissionUiState.Paused) {
            viewModel.onAction(LocationPermissionAction.OnPermissionsDenied)
        }
        onPauseOrDispose {
            if (uiState.value is LocationPermissionUiState.Denied) {
                viewModel.onAction(LocationPermissionAction.OnPause)
            }
        }
    }

    val fineLocationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            COARSE_LOCATION,
            FINE_LOCATION
        ),
        onPermissionsResult = { permissionMap ->
            val areGranted = permissionMap.values.reduce { acc, next ->
                acc && next
            }
            if (areGranted) {
                viewModel.onAction(LocationPermissionAction.OnPermissionsGranted)
            } else {
                viewModel.onAction(LocationPermissionAction.OnPermissionsDenied)
            }
        }
    )

    Box(modifier = Modifier.safeContentPadding()) {
        when (uiState.value) {
            is LocationPermissionUiState.Loading, LocationPermissionUiState.Paused -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LocationPermissionUiState.Denied -> {
                LocationPermissionContent(
                    title = R.string.precise_location_open_config_title,
                    description = R.string.precise_location_open_config_description,
                    buttonLabel = R.string.precise_location_open_config_button_label,
                    onButtonClick = {
                        openAppDetailsSettings(context)
                    }
                )
            }

            is LocationPermissionUiState.RequestPermission -> {
                LocationPermissionContent(
                    title = R.string.precise_location_title,
                    description = R.string.precise_location_description,
                    buttonLabel = R.string.precise_location_button_label,
                    onButtonClick = {
                        fineLocationPermissionState.launchMultiplePermissionRequest()
                    }
                )
            }

            is LocationPermissionUiState.Granted -> onLocationPermissionGranted.invoke()
        }
    }
}

private fun openAppDetailsSettings(context: Context) {
    val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.setData(uri)
    context.startActivity(intent)
}


@Composable
private fun LocationPermissionContent(
    @StringRes title: Int,
    @StringRes description: Int,
    @StringRes buttonLabel: Int,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier.size(200.dp),
                    painter = painterResource(R.drawable.image_pin),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = stringResource(description),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onButtonClick,
            content = {
                Text(text = stringResource(buttonLabel))
            }
        )
    }
}

private fun hasLocationPermissions(context: Context): Boolean {
    return hasPreciseLocationPermission(context) && hasApproximateLocationPermission(context)
}

private fun hasPreciseLocationPermission(context: Context): Boolean {
    return context.checkSelfPermission(FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

private fun hasApproximateLocationPermission(context: Context): Boolean {
    return context.checkSelfPermission(COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

@Preview(showBackground = true)
@Composable
private fun PreciseLocationPermissionContentPreview() {
    LocationPermissionContent(
        title = R.string.precise_location_title,
        description = R.string.precise_location_description,
        buttonLabel = R.string.precise_location_button_label,
        onButtonClick = {}
    )
}