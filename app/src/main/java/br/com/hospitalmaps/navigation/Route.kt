package br.com.hospitalmaps.navigation

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    data object LocationPermission

    @Serializable
    data object Home

    @Serializable
    data object Navigation

    @Serializable
    data object PersonalInfo
}