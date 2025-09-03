package com.example.annotationmarker

import android.view.ViewGroup
import com.example.annotationmarker.ClickManager.Companion.DEFAULT_HITBOX_SIZE
import com.example.annotationmarker.source.MapSourceManager
import com.example.annotationmarker.util.Constants
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.geojson.Feature

class AnnotationMarkerManager(
    private val mapView: MapView,
    private val container: ViewGroup,
) {

    companion object {
        const val DEFAULT_SOURCE_ID = "defaultSource"
    }

    lateinit var mapSourceManager: MapSourceManager
    lateinit var clickManager: ClickManager

    init {
        mapView.getMapAsync { map ->
            map.style.let {
                mapSourceManager = MapSourceManager(it)
                clickManager = ClickManager(map)
            }
        }
    }

    // Добавление фич опеределенного типа маркеров (без обновления/замены)
    fun addToSourceByTypeId(
        sourceId: String = DEFAULT_SOURCE_ID,
        features: List<Feature>
    ) {
        val existing = mapSourceManager.getFeaturesFromSource(sourceId).toMutableList()
        existing.addAll(features)
        mapSourceManager.updateData(sourceId, existing)
    }

    // Обновление фич по типу маркеров
    fun updateSource(
        sourceId: String = DEFAULT_SOURCE_ID,
        uniqueMarkerType: String,
        newFeatures: List<Feature>
    ) {
        val withoutOld = mapSourceManager.getFeaturesWithoutPropertyValue(
            sourceId,
            Constants.UNIQUE_MARKER_TYPE_PROPERTY,
            uniqueMarkerType
        ).toMutableList()

        withoutOld.addAll(newFeatures)
        mapSourceManager.updateData(sourceId, withoutOld)
    }

    // Удаление всех обьектов по типу маркеров
    fun deleteFromSourceByMarkerType(
        sourceId: String = DEFAULT_SOURCE_ID,
        uniqueMarkerType: String
    ) {
        val without = mapSourceManager.getFeaturesWithoutPropertyValue(
            sourceId,
            Constants.UNIQUE_MARKER_TYPE_PROPERTY,
            uniqueMarkerType
        )
        mapSourceManager.updateData(sourceId, without)
    }

    //  получить все обьекты по типу маркеров
    fun getFeaturesByTypeId(
        sourceId: String = DEFAULT_SOURCE_ID,
        uniqueMarkerType: String
    ): List<Feature> {
        return mapSourceManager.getFeaturesByPropertyValue(
            sourceId,
            Constants.UNIQUE_MARKER_TYPE_PROPERTY,
            uniqueMarkerType
        )
    }

    // добавить слушатель нажатий
    fun addClickListenerForMarkerType(
        uniqueMarkerType: String,
        hitbox: Float = DEFAULT_HITBOX_SIZE,
        onClick: (Feature, LatLng) -> Unit
    ) {
        clickManager.addClickListener(uniqueMarkerType, hitbox, onClick)
    }

    // добавить слушатель длинных нажатий
    fun addLongClickListenerForMarkerType(
        uniqueMarkerType: String,
        hitbox: Float = DEFAULT_HITBOX_SIZE,
        onLongClick: (Feature, LatLng) -> Unit
    ) {
        clickManager.addLongClickListener(uniqueMarkerType, hitbox, onLongClick)
    }
}



