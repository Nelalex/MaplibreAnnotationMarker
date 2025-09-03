package com.example.annotationmarker.util

import org.maplibre.android.geometry.LatLng
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object Extensions {
    fun generateArcPoints(
        radius: Double,
        startPoint: LatLng,
        leftAngle: Double,
        rightAngle: Double,
        direction: Float
    ): List<LatLng> {
        val points = mutableListOf<LatLng>()

        val earthRadius = 6378137.0

        // Длина дуги сектора
        val sweepAngle = leftAngle + rightAngle
        val arcLength = (sweepAngle * Math.PI * radius) / 180.0

        // Определяем шаг точек
        val stepSize = when {
            arcLength < 200 -> 1
            arcLength < 400 -> 2
            arcLength < 800 -> 4
            arcLength < 1600 -> 8
            arcLength < 3200 -> 16
            arcLength < 6400 -> 32
            arcLength < 12800 -> 64
            arcLength < 25600 -> 128
            arcLength < 51200 -> 256
            arcLength < 102400 -> 512
            else -> 800
        }

        val steps = (arcLength / stepSize).toInt().coerceAtLeast(1)

        val startAngle = direction - leftAngle   // Левый край сектора
        val endAngle = direction + rightAngle    // Правый край сектора

        for (i in 0..steps) {
            val angle = Math.toRadians((startAngle + ((endAngle - startAngle) * i / steps)))

            val deltaLat = (radius / earthRadius) * Math.cos(angle)
            val deltaLon =
                (radius / earthRadius) * Math.sin(angle) / Math.cos(Math.toRadians(startPoint.latitude))

            val newLat = startPoint.latitude + Math.toDegrees(deltaLat)
            val newLon = startPoint.longitude + Math.toDegrees(deltaLon)

            points.add(LatLng(newLat, newLon))
        }
        return points
    }

    /** Рассчитывает координаты точки по стартовой позиции, смещению и азимуту. */
    fun computeOffset(from: LatLng, distanceMeters: Double, bearingDeg: Double): LatLng {
        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)
        val bearing = Math.toRadians(bearingDeg)
        val distanceRatio = distanceMeters / 6371000.0

        val sinLat1 = sin(lat1)
        val cosLat1 = cos(lat1)
        val sinDistance = sin(distanceRatio)
        val cosDistance = cos(distanceRatio)

        val sinLat2 = sinLat1 * cosDistance + cosLat1 * sinDistance * cos(bearing)
        val lat2 = asin(sinLat2)

        val y = sin(bearing) * sinDistance * cosLat1
        val x = cosDistance - sinLat1 * sinLat2
        var lon2 = lon1 + atan2(y, x)

        // нормализация долготы
        lon2 = (lon2 + Math.PI + Math.PI) % (2 * Math.PI) - Math.PI

        return LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }

    fun Int.toHexColor(): String = String.format("#%06X", (0xFFFFFF and this))
}