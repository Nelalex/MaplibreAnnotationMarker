package com.example.annotationmarker.features

import android.graphics.Bitmap
import com.example.annotationmarker.util.Constants
import com.example.annotationmarker.util.Extensions.computeOffset
import com.example.annotationmarker.util.Extensions.generateArcPoints
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import kotlin.math.atan2
import kotlin.math.hypot

object MaplibreObjectsUtil {

    fun createLineFeatures(
        points: List<LatLng>,
        params: FeatureParams.LineParams,
        connectAll: Boolean = true
    ): List<Feature> {
        val features = mutableListOf<Feature>()
        if (points.size < 2) return features

        if (connectAll) {
            for (i in 0 until points.size - 1) {
                features += createLineFeature(listOf(points[i], points[i + 1]), params)
            }
        } else {
            for (i in points.indices step 2) {
                if (i + 1 < points.size) {
                    features += createLineFeature(listOf(points[i], points[i + 1]), params)
                }
            }
        }
        return features
    }

    fun createLineFeature(points: List<LatLng>, params: FeatureParams.LineParams): Feature {
        val line =
            LineString.fromLngLats(points.map { Point.fromLngLat(it.longitude, it.latitude) })
        return Feature.fromGeometry(line).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.LINE_COLOR_PROPERTY, params.lineColor)
            addNumberProperty(Constants.LINE_WIDTH_PROPERTY, params.lineWidth)
        }
    }

    fun createPolygonFeature(
        points: List<LatLng>,
        params: FeatureParams.FillParams
    ): Feature? {
        if (points.size < 3) {
            return null
        }

        // Преобразуем LatLng -> Point
        var ring = points.map { Point.fromLngLat(it.longitude, it.latitude) }

        // Проверяем замкнутость: если первая и последняя не совпадают → добавляем
        if (ring.first() != ring.last()) {
            ring = ring + ring.first()
        }

        // Оборачиваем в список списков (GeoJSON требует список колец)
        val polygon = Polygon.fromLngLats(listOf(ring))

        return Feature.fromGeometry(polygon).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.FILL_COLOR_PROPERTY, params.fillColor)
            addNumberProperty(Constants.FILL_OPACITY_PROPERTY, params.fillOpacity)
        }
    }

    fun createCircleFeature(
        center: LatLng,
        radius: Double,
        direction: Float,
        params: FeatureParams.FillParams
    ): Feature {
        val arcPoints = generateArcPoints(
            radius = radius,
            startPoint = center,
            leftAngle = 0.0,
            rightAngle = 360.0,
            direction = direction
        ).map { Point.fromLngLat(it.longitude, it.latitude) }

        val closed = if (arcPoints.isNotEmpty()) arcPoints + arcPoints.first() else arcPoints
        val polygon = Polygon.fromLngLats(listOf(closed))

        return Feature.fromGeometry(polygon).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.FILL_COLOR_PROPERTY, params.fillColor)
            addNumberProperty(Constants.FILL_OPACITY_PROPERTY, params.fillOpacity)
        }
    }

    fun createSectorFeature(
        center: LatLng,
        leftSectorAngle: Double,
        rightSectorAngle: Double,
        radius: Double,
        direction: Float,
        params: FeatureParams.FillParams
    ): Feature {
        val arcPoints = generateArcPoints(
            radius = radius,
            startPoint = center,
            leftAngle = leftSectorAngle,
            rightAngle = rightSectorAngle,
            direction = direction
        ).map { Point.fromLngLat(it.longitude, it.latitude) }

        val startPoint = Point.fromLngLat(center.longitude, center.latitude)
        val closed = listOf(startPoint) + arcPoints + startPoint
        val polygon = Polygon.fromLngLats(listOf(closed))

        return Feature.fromGeometry(polygon).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.FILL_COLOR_PROPERTY, params.fillColor)
            addNumberProperty(Constants.FILL_OPACITY_PROPERTY, params.fillOpacity)
        }
    }

    fun createCircleOutlineFeature(
        center: LatLng,
        radius: Double,
        direction: Float,
        params: FeatureParams.LineParams
    ): Feature {
        val arcPoints = generateArcPoints(
            radius = radius,
            startPoint = center,
            leftAngle = 0.0,
            rightAngle = 360.0,
            direction = direction
        ).map { Point.fromLngLat(it.longitude, it.latitude) }

        val closed = if (arcPoints.isNotEmpty()) arcPoints + arcPoints.first() else arcPoints
        val line = LineString.fromLngLats(closed)

        return Feature.fromGeometry(line).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.LINE_COLOR_PROPERTY, params.lineColor)
            addNumberProperty(Constants.LINE_WIDTH_PROPERTY, params.lineWidth)
        }
    }

    fun createBaseSymbolFeature(
        coordinates: LatLng,
        params: FeatureParams.IconParams,
        iconBitmap: Bitmap,
        icon: String? = null,
        style: Style?
    ): Feature? {
        if (style == null) return null
        icon?.let {
            style.getImage(it) == null
            style.addImageAsync(icon, iconBitmap)
        }
        val point = Point.fromLngLat(coordinates.longitude, coordinates.latitude)
        return Feature.fromGeometry(point).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addNumberProperty(Constants.ICON_SIZE_PROPERTY, params.iconSize)
            addStringProperty(Constants.UNIQUE_ICON_NAME_PROPERTY, icon ?: "")
        }
    }

    fun createTextFeature(
        coordinates: LatLng,
        text: String,
        params: FeatureParams.TextParams
    ): Feature {
        val point = Point.fromLngLat(coordinates.longitude, coordinates.latitude)
        return Feature.fromGeometry(point).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.TEXT_PROPERTY, text)
            addStringProperty(Constants.TEXT_COLOR_PROPERTY, params.textColor)
            addNumberProperty(Constants.TEXT_SIZE_PROPERTY, params.textSize)
            addStringProperty(Constants.TEXT_HALO_COLOR_PROPERTY, params.textHaloColor)
            addNumberProperty(Constants.TEXT_HALO_WIDTH_PROPERTY, params.textHaloWidth)
        }
    }

    /** Квадрат по центру, расстоянию до вершины и азимуту. */
    fun createSquareFeature(
        center: LatLng,
        distanceToVertexMeters: Double,
        azimuthDeg: Double = 0.0,
        params: FeatureParams.FillParams
    ): Feature {
        val baseAngles = listOf(45.0, 135.0, 225.0, 315.0)
        val vertices = baseAngles.map { angle ->
            val heading = azimuthDeg + angle
            val point = computeOffset(center, distanceToVertexMeters, heading)
            Point.fromLngLat(point.longitude, point.latitude)
        }

        val ring = vertices + vertices.first()
        val polygon = Polygon.fromLngLats(listOf(ring))

        return Feature.fromGeometry(polygon).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.FILL_COLOR_PROPERTY, params.fillColor)
            addNumberProperty(Constants.FILL_OPACITY_PROPERTY, params.fillOpacity)
        }
    }

    /** Прямоугольник по центру, ширине, высоте и азимуту. */
    fun createRectangleFeature(
        center: LatLng,
        widthMeters: Double,
        heightMeters: Double,
        azimuthDeg: Double = 0.0,
        params: FeatureParams.FillParams
    ): Feature {
        val halfWidth = widthMeters / 2.0
        val halfHeight = heightMeters / 2.0

        // четыре угла прямоугольника относительно центра
        val cornerBearings = listOf(
            atan2(halfWidth, halfHeight),     // top-right
            atan2(-halfWidth, halfHeight),    // top-left
            atan2(-halfWidth, -halfHeight),   // bottom-left
            atan2(halfWidth, -halfHeight)     // bottom-right
        )

        val cornerDistances = listOf(
            hypot(halfWidth, halfHeight),
            hypot(halfWidth, halfHeight),
            hypot(halfWidth, halfHeight),
            hypot(halfWidth, halfHeight)
        )

        val vertices = cornerBearings.mapIndexed { index, angle ->
            val bearingDeg = Math.toDegrees(angle) + azimuthDeg
            val point = computeOffset(center, cornerDistances[index], bearingDeg)
            Point.fromLngLat(point.longitude, point.latitude)
        }

        val ring = vertices + vertices.first()
        val polygon = Polygon.fromLngLats(listOf(ring))

        return Feature.fromGeometry(polygon).apply {
            addNumberProperty(Constants.UNIQUE_MARKER_ID_PROPERTY, params.objectId)
            addStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY, params.uniqueType)
            addBooleanProperty(Constants.CLICKABLE_PROPERTY, params.clickable)
            addStringProperty(Constants.FILL_COLOR_PROPERTY, params.fillColor)
            addNumberProperty(Constants.FILL_OPACITY_PROPERTY, params.fillOpacity)
        }
    }
}

fun Feature.getDatabaseId() = this.getStringProperty("id")?.toIntOrNull() ?: 0