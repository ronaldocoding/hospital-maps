package br.com.hospitalmaps.shared.utils

import android.content.Context
import br.com.hospitalmaps.R
import kotlin.math.roundToInt

object DistanceFormatter {

    fun formatDistance(context: Context, distanceInMeters: Float): String {
        return when {
            distanceInMeters < 1000 -> {
                // Show meters for distances less than 1km
                val meters = distanceInMeters.roundToInt()
                context.getString(R.string.distance_in_meters, meters)
            }
            distanceInMeters < 10000 -> {
                // Show 1 decimal place for distances 1-10km
                val kilometers = distanceInMeters / 1000f
                context.getString(R.string.distance_in_kilometers_precise, String.format("%.1f", kilometers))
            }
            else -> {
                // Show whole numbers for distances > 10km
                val kilometers = (distanceInMeters / 1000f).roundToInt()
                context.getString(R.string.distance_in_kilometers, kilometers)
            }
        }
    }
}
