package com.example.annotationmarker

import android.graphics.RectF
import com.example.annotationmarker.util.Constants
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.geojson.Feature

class ClickManager(
    private val map: MapLibreMap
) {

    companion object {
        const val DEFAULT_HITBOX_SIZE = 10f
    }

    private data class ClickHandler(
        val uniqueMarkerType: String,
        val hitbox: Float = DEFAULT_HITBOX_SIZE,
        val onClick: (Feature, LatLng) -> Unit
    )

    private data class LongClickHandler(
        val uniqueMarkerType: String,
        val hitbox: Float = DEFAULT_HITBOX_SIZE,
        val onLongClick: (Feature, LatLng) -> Unit
    )

    private val clickHandlers = mutableListOf<ClickHandler>()
    private val longClickHandlers = mutableListOf<LongClickHandler>()

    init {
        map.addOnMapClickListener { point ->
            clickHandlers.any { handler ->
                val features = queryClickableFeatures(point, handler.hitbox)
                val matched = selectFeatureByTypeAndId(features, handler.uniqueMarkerType)
                matched?.let {
                    handler.onClick(it, point)
                    true
                } ?: false
            }
        }

        map.addOnMapLongClickListener { point ->
            longClickHandlers.any { handler ->
                val features = queryClickableFeatures(point, handler.hitbox)
                val matched = selectFeatureByTypeAndId(features, handler.uniqueMarkerType)
                matched?.let {
                    handler.onLongClick(it, point)
                    true
                } ?: false
            }
        }
    }

    fun addClickListener(
        uniqueMarkerType: String,
        hitbox: Float = DEFAULT_HITBOX_SIZE,
        onClick: (Feature, LatLng) -> Unit
    ) {
        clickHandlers.add(ClickHandler(uniqueMarkerType, hitbox, onClick))
    }

    fun addLongClickListener(
        uniqueMarkerType: String,
        hitbox: Float = DEFAULT_HITBOX_SIZE,
        onLongClick: (Feature, LatLng) -> Unit
    ) {
        longClickHandlers.add(LongClickHandler(uniqueMarkerType, hitbox, onLongClick))
    }

    fun removeAllClickListeners() {
        clickHandlers.clear()
    }

    fun removeAllLongClickListeners() {
        longClickHandlers.clear()
    }

    private fun queryClickableFeatures(point: LatLng, hitbox: Float): List<Feature> {
        val screenPoint = map.projection.toScreenLocation(point)
        val rect = RectF(
            screenPoint.x - hitbox,
            screenPoint.y - hitbox,
            screenPoint.x + hitbox,
            screenPoint.y + hitbox
        )
        return map.queryRenderedFeatures(rect)
            .filter { it.getBooleanProperty(Constants.CLICKABLE_PROPERTY) ?: false }
    }

    private fun selectFeatureByTypeAndId(features: List<Feature>, type: String): Feature? {
        return features
            .filter { it.getStringProperty(Constants.UNIQUE_MARKER_TYPE_PROPERTY) == type }
            .minByOrNull {
                it.getStringProperty(Constants.UNIQUE_MARKER_ID_PROPERTY)?.toIntOrNull()
                    ?: Int.MAX_VALUE
            }
    }
}
