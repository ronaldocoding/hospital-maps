package br.com.hospitalmaps.presentation.home.action

sealed class HomeAction {
    data object OnInit : HomeAction()
    data object OnMapLoaded : HomeAction()
}