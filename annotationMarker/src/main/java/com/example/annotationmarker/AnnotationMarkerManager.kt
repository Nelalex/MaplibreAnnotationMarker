package com.example.annotationmarker

import android.util.Log
import com.example.annotationmarker.ClickManager.Companion.DEFAULT_HITBOX_SIZE
import com.example.annotationmarker.source.MapSourceManager
import com.example.annotationmarker.util.Constants
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.geojson.Feature

class AnnotationMarkerManager(
    private val mapView: MapView
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

    /**
     * Добавляет к источнику данных новые обьекты (без обновления и замен)
     *
     * @param sourceId Идентификатор источника.
     * @param features Новый список фич для отображения.
     */
    fun addToSource(
        sourceId: String = DEFAULT_SOURCE_ID,
        features: List<Feature>
    ) {
        Log.d("addToSource", features.toString())
        val existing = mapSourceManager.getFeaturesFromSource(sourceId).toMutableList()
        existing.addAll(features)
        mapSourceManager.updateData(sourceId, existing)
    }

    /**
     * Обновляет источник данных новыми метками определенного типа, при совпадении uniqueMarkerType
     * и id перезаписывает метку
     *
     * @param sourceId Идентификатор источника.
     * @param uniqueMarkerType Уникальный индентифиактор группы меток со схожим поведением
     * (кликабельность, инфоокна и т.д.)
     * @param newFeatures Новый список фич для отображения.
     */
    fun updateSource(
        sourceId: String = DEFAULT_SOURCE_ID,
        uniqueMarkerType: String,
        newFeatures: List<Feature>
    ) {
        // старые метки других типов
        val withoutOld = mapSourceManager
            .getFeaturesWithoutPropertyValue(
                sourceId,
                Constants.UNIQUE_MARKER_TYPE_PROPERTY,
                uniqueMarkerType
            )
            .toMutableList()

        // старые метки нужного типа
        val oldSameMarkerType = mapSourceManager
            .getFeaturesByPropertyValue(
                sourceId,
                Constants.UNIQUE_MARKER_TYPE_PROPERTY,
                uniqueMarkerType
            )

        val oldMap = oldSameMarkerType.associateBy { it.getStringProperty("id") }

        val updatedSameMarkerType = newFeatures.map { newFeature ->
            val id = newFeature.getStringProperty("id")
            val oldFeature = oldMap[id]
            oldFeature?.let {
                newFeature
            } ?: newFeature
        }

        val finalList = withoutOld + updatedSameMarkerType

        mapSourceManager.updateData(sourceId, finalList)
    }

    /**
     * Удаляет из источника данных все метки определенного типа
     * @param sourceId Идентификатор источника.
     * @param uniqueMarkerType Уникальный индентифиактор группы меток со схожим поведением
     * (кликабельность, инфоокна и т.д.)
     */
    fun deleteAllFromSourceByMarkerType(
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

    /**
     * Удаляет из источника данных метки определенного типа по id
     * @param sourceId Идентификатор источника.
     * @param uniqueMarkerType Уникальный индентифиактор группы меток со схожим поведением
     * (кликабельность, инфоокна и т.д.)
     */
    fun deleteFromSourceById(
        sourceId: String = DEFAULT_SOURCE_ID,
        uniqueMarkerType: String,
        listIds: List<String>
    ) {
        val withoutOld = mapSourceManager.getFeaturesWithoutPropertyValue(
            sourceId,
            Constants.UNIQUE_MARKER_TYPE_PROPERTY,
            uniqueMarkerType
        )

        // старые метки нужного типа
        val oldSameMarkerType = mapSourceManager
            .getFeaturesByPropertyValue(
                sourceId,
                Constants.UNIQUE_MARKER_TYPE_PROPERTY,
                uniqueMarkerType
            ).associateBy { it.getStringProperty("id") }
            .toMutableMap()

        oldSameMarkerType.keys.removeAll(listIds)

        val filteredList: List<Feature> = oldSameMarkerType.values.toList()

        val finalList = withoutOld + filteredList

        mapSourceManager.updateData(sourceId, finalList)
    }

    /**
     * Получает список обьектов из источника данных определенной группы меток
     * @param sourceId Идентификатор источника.
     * @param uniqueMarkerType Уникальный индентифиактор группы меток со схожим поведением
     * (кликабельность, инфоокна и т.д.)
     */
    fun getFeaturesByMarkerType(
        sourceId: String = DEFAULT_SOURCE_ID,
        uniqueMarkerType: String
    ): List<Feature> {
        return mapSourceManager.getFeaturesByPropertyValue(
            sourceId,
            Constants.UNIQUE_MARKER_TYPE_PROPERTY,
            uniqueMarkerType
        )
    }

    /**
     * Получает список обьектов из источника данных определенной группы меток
     * @param sourceId Идентификатор источника.
     * @param uniqueMarkerType Уникальный индентифиактор группы меток со схожим поведением
     * (кликабельность, инфоокна и т.д.)
     */
    fun getFeaturesByIds(
        sourceId: String = DEFAULT_SOURCE_ID,
        uniqueMarkerType: String,
        listIds: List<String>
    ): List<Feature> {
        val features = mapSourceManager
            .getFeaturesByPropertyValue(
                sourceId,
                Constants.UNIQUE_MARKER_TYPE_PROPERTY,
                uniqueMarkerType
            ).associateBy { it.getStringProperty("id") }
            .toMutableMap()

        features.keys.retainAll(listIds)

        return features.values.toList()
    }

    /**
     * Добавляет слушатель нажатий карты по любому обьекту определенного типа
     * @param uniqueMarkerType Уникальный индентифиактор группы меток со схожим поведением
     * (кликабельность, инфоокна и т.д.)
     * @param onClick Наша повдеение при клике (в ней можно получить сам примитив с карты (Feature),
     * в котором хранится его уникальный id и параметры визуализаии, а также его географические координаты
     * @param hitbox Хитбокс(в пикселях) при клике по обьекту
     */
    fun addClickListenerForMarkerType(
        uniqueMarkerType: String,
        hitbox: Float = DEFAULT_HITBOX_SIZE,
        onClick: (Feature, LatLng) -> Unit
    ) {
        clickManager.addClickListener(uniqueMarkerType, hitbox, onClick)
    }

    /**
     * Добавляет слушатель длительных нажатий карты по любому обьекту определенного типа
     * @param uniqueMarkerType Уникальный индентифиактор группы меток со схожим поведением
     * (кликабельность, инфоокна и т.д.)
     * @param onLongClick Наша повдеение при длинном клике (в ней можно получить сам примитив с карты (Feature),
     * в котором хранится его уникальный id и параметры визуализаии, а также его географические координаты
     * @param hitbox Хитбокс(в пикселях) при клике по обьекту
     */
    fun addLongClickListenerForMarkerType(
        uniqueMarkerType: String,
        hitbox: Float = DEFAULT_HITBOX_SIZE,
        onLongClick: (Feature, LatLng) -> Unit
    ) {
        clickManager.addLongClickListener(uniqueMarkerType, hitbox, onLongClick)
    }
}



