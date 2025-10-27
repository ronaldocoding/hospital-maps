package br.com.hospitalmaps.data.model

data class UserLocationData(
    val latitude: Double,
    val longitude: Double
) {
    fun isEmpty() = latitude == 0.0 && longitude == 0.0
}