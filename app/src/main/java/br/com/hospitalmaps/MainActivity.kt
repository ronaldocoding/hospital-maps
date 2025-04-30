package br.com.hospitalmaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.hospitalmaps.locationpermission.view.LocationPermissionScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            /*val viewModel: MainViewModel = koinViewModel()
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
            }*/
            LocationPermissionScreen()
        }

    }
}