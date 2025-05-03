package br.com.hospitalmaps.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

@SuppressLint("MissingPermission")
class UserLocationRepository(
    private val locationProviderClient: FusedLocationProviderClient,
    private val context: Context
) {
    fun getUserLastLocation(): Flow<Location> = callbackFlow {
        if (hasNotLocationPermissions()) throw SecurityException(
            "The permissions ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION are required"
        )
        locationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                runCatching { trySend(requireNotNull(location)) }
                    .onFailure { close(it) }
            }.addOnFailureListener {
                close(it)
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    private fun hasNotLocationPermissions() =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
}
