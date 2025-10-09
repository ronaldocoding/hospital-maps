package br.com.hospitalmaps.presentation.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import br.com.hospitalmaps.R
import br.com.hospitalmaps.databinding.NavigationFragmentLayoutBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.RoutingOptions
import com.google.android.libraries.navigation.SupportNavigationFragment
import com.google.android.libraries.navigation.Waypoint

private const val TAG = "NavigationScreen"

@SuppressLint("MissingPermission")
@Composable
fun NavigationScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val destinationLatLngString = navController.previousBackStackEntry?.savedStateHandle?.get<String>("destinationLatLng")
    val destinationLatLngValues = destinationLatLngString?.split(",")?.mapNotNull { it.toDoubleOrNull() }
    val destinationLatLng = LatLng(destinationLatLngValues?.get(0) ?: 0.0, destinationLatLngValues?.get(1) ?: 0.0)
    Log.d(TAG, "destinationLatLng: $destinationLatLng")
    val navigationFragment = remember { SupportNavigationFragment() }
    var navigator by remember { mutableStateOf<Navigator?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                NavigationApi.getNavigator(context as FragmentActivity,
                    object : NavigationApi.NavigatorListener {
                        override fun onNavigatorReady(nav: Navigator) {
                            navigator = nav
                            navigationFragment.getMapAsync { googleMap ->
                                googleMap.followMyLocation(GoogleMap.CameraPerspective.TILTED)
                            }
                            destinationLatLng?.let { latLng ->
                                Log.d(TAG, "Calling startNavigation with latLng: $latLng")
                                startNavigation(nav, navigationFragment, latLng)
                            }
                        }

                        override fun onError(errorCode: Int) {
                            Log.e(TAG, "Navigation SDK Error: $errorCode")
                        }
                    })
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            navigator = null
        }
    }

    AndroidViewBinding(NavigationFragmentLayoutBinding::inflate) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.navigation_fragment, navigationFragment)
            .commitNow()
    }
}

@SuppressLint("MissingPermission")
private fun startNavigation(
    navigator: Navigator,
    navFragment: SupportNavigationFragment,
    destination: LatLng
) {
    val destinationWaypoint = Waypoint.builder()
        .setLatLng(destination.latitude, destination.longitude)
        .build()

    val routingOptions = RoutingOptions()
        .travelMode(RoutingOptions.TravelMode.DRIVING)

    navigator.setDestination(destinationWaypoint, routingOptions)
        .setOnResultListener { routeStatus ->
            when (routeStatus) {
                Navigator.RouteStatus.OK -> {
                    navigator.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)
                    navigator.startGuidance()
                    navFragment.getMapAsync { googleMap ->
                        googleMap.isMyLocationEnabled = true
                    }
                }
                Navigator.RouteStatus.NO_ROUTE_FOUND -> {
                    Log.e(TAG, "No route found")
                }
                Navigator.RouteStatus.NETWORK_ERROR -> {
                    Log.e(TAG, "Network error")
                }
                Navigator.RouteStatus.ROUTE_CANCELED -> {
                    Log.e(TAG, "Route canceled")
                }
                else -> {
                    Log.e(TAG, "Unexpected route status: $routeStatus")
                }
            }
        }
}