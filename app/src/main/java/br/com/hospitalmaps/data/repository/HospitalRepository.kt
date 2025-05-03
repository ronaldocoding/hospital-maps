package br.com.hospitalmaps.data.repository

import android.location.Location
import br.com.hospitalmaps.data.model.HospitalData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

private const val RADIUS_SEARCH = 10000.0
private const val HOSPITAL_TYPE = "hospital"
private const val MAX_RESULT_COUNT = 10
private const val LOCATION_PROVIDER = ""

class HospitalRepository(private val placesClient: PlacesClient) {
    fun getNearbyHospitals(centerLocation: Location): Flow<List<HospitalData>> = callbackFlow {
        val placeFields = listOf(Place.Field.DISPLAY_NAME, Place.Field.LOCATION)
        val center = LatLng(centerLocation.latitude, centerLocation.longitude)
        val circle = CircularBounds.newInstance(center, RADIUS_SEARCH)
        val includedTypes = listOf(HOSPITAL_TYPE)
        val searchNearbyHospitalsRequest = SearchNearbyRequest.builder(circle, placeFields)
            .setIncludedTypes(includedTypes)
            .setMaxResultCount(MAX_RESULT_COUNT)
            .setRankPreference(SearchNearbyRequest.RankPreference.POPULARITY)
            .build()
        val locationHelper = Location(LOCATION_PROVIDER)
        placesClient.searchNearby(searchNearbyHospitalsRequest)
            .addOnSuccessListener {
                runCatching {
                    trySend(
                        it.places.map {
                            locationHelper.longitude = it?.location?.longitude ?: 0.0
                            locationHelper.latitude = it?.location?.latitude ?: 0.0
                            HospitalData(
                                name = it?.displayName ?: "",
                                longitude = locationHelper.longitude,
                                latitude = locationHelper.latitude,
                                distanceFromCenter = centerLocation.distanceTo(locationHelper)
                            )
                        }.sortedBy { it.distanceFromCenter }
                    )
                }.onFailure { close(it) }
            }
            .addOnFailureListener {
                close(it)
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)
}

