package br.com.hospitalmaps.data.model

data class HospitalData(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceFromCenter: Float,
    val placeId: String
)