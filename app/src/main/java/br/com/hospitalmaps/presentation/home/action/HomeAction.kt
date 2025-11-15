package br.com.hospitalmaps.presentation.home.action

import com.google.android.gms.maps.model.LatLng

sealed class HomeAction {
    data object OnInitialized : HomeAction()
    data object OnMapLoaded : HomeAction()
    data object OnTryAgainClicked : HomeAction()
    data class OnZoomInUserClicked(val userPoint: LatLng) : HomeAction()
    data object OnZoomInMapClicked : HomeAction()
    data object OnZoomOutMapClicked : HomeAction()
}