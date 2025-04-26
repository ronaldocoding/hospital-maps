package br.com.hospitalmaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.view.viewmodel.MainViewModel
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private var permissionsListener: PermissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {}

        override fun onPermissionResult(granted: Boolean) {}

    }

    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (PermissionsManager.areLocationPermissionsGranted(this).not()) {
                permissionsManager = PermissionsManager(permissionsListener)
                permissionsManager.requestLocationPermissions(this)
            }
            val viewModel: MainViewModel = koinViewModel()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            viewModel.getNearestHospital()

            Column(modifier = Modifier.fillMaxSize()) {
                when (uiState.value.isLoading) {
                    true -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    else -> {
                        Text("Nearest Hospital's Name: ${uiState.value.nearestHospital.name}")
                        Text("Nearest Hospital's Latitude: ${uiState.value.nearestHospital.latitude}")
                        Text("Nearest Hospital's Longitude: ${uiState.value.nearestHospital.longitude}")
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        permissionsManager.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }
}