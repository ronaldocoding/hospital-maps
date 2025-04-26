package br.com.hospitalmaps.domain.model

import android.location.Location

data class HospitalData(
    val name: String,
    val location: Location
)