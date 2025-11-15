package br.com.hospitalmaps.presentation.home.event

import com.google.android.gms.maps.model.LatLng

sealed interface HomeEvent {
    data class ZoomInUser(val userPoint: LatLng) : HomeEvent
    data object ZoomInMap : HomeEvent
    data object ZoomOutMap : HomeEvent
}
