package com.fcpdeveloperstudio.vanetsimulation

import kotlin.math.*

object GeoUtils {
    private const val EARTH_RADIUS_KM = 6371.0
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val originLat = Math.toRadians(lat1)
        val destinationLat = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2) +
                sin(dLon / 2).pow(2) * cos(originLat) * cos(destinationLat)

        val c = 2 * asin(sqrt(a))

        return Math.round((EARTH_RADIUS_KM * c) * 100) / 100.0
    }
}