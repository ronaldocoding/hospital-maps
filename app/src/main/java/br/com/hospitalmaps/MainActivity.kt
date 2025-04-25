package br.com.hospitalmaps

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

class MainActivity : ComponentActivity() {

    private var permissionsListener: PermissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {}

        override fun onPermissionResult(granted: Boolean) {}

    }

    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = BuildConfig.PLACES_API_KEY

        if (apiKey.isEmpty()) {
            Log.e("Places test", "No api key")
            finish()
            return
        }

        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

        val placesClient = Places.createClient(this)

        val placeFields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION)

        val center = LatLng(-3.118078, -59.977806)

        val circle = CircularBounds.newInstance(center, 10000.0)

        val includedTypes = listOf("hospital")

        val searchNearbyRequest = SearchNearbyRequest.builder(circle, placeFields)
            .setIncludedTypes(includedTypes)
            .setMaxResultCount(10)
            .setRankPreference(SearchNearbyRequest.RankPreference.POPULARITY)
            .build()

        placesClient.searchNearby(searchNearbyRequest)
            .addOnSuccessListener {
                val places = it.places

                data class PlaceData(
                    val id: String?,
                    val displayName: String?,
                    val distance: Float?
                )

                val centerLocation = Location("")
                centerLocation.latitude = center.latitude
                centerLocation.longitude = center.longitude

                val distances = mutableListOf<PlaceData>()

                places.forEach { place ->
                    val placeLocation = Location("")
                    placeLocation.latitude = place.location?.latitude ?: 0.0
                    placeLocation.longitude = place.location?.longitude ?: 0.0
                    distances.add(
                        PlaceData(
                            place.id,
                            place.displayName,
                            centerLocation.distanceTo(placeLocation)
                        )
                    )
                }

                distances.sortBy { distance -> distance.distance }


                places.forEach { place ->
                    Log.d(
                        "MainActivity",
                        "ID: ${place.id}, Name: ${place.displayName}, Location: ${place.location}"
                    )
                }

                Log.d("MainActivity", "Closest hospital: ${distances.first()}")

                // Log all distances
                distances.forEach { place ->
                    Log.d(
                        "MainActivity",
                        "ID: ${place.id}, Name: ${place.displayName}, Distance: ${place.distance}"
                    )
                }
            }
            .addOnFailureListener {
                Log.e("MainActivity", "Error: ${it.message}")
            }

        if (PermissionsManager.areLocationPermissionsGranted(this).not()) {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }

        setContent {
            val mapViewportState = rememberMapViewportState()
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
            ) {
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                        puckBearing = PuckBearing.COURSE
                        puckBearingEnabled = true
                    }
                    mapViewportState.transitionToFollowPuckState()
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