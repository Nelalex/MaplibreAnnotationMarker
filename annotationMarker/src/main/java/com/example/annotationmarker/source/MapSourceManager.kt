package com.example.annotationmarker.source

import android.util.Log
import com.example.annotationmarker.layer.LayerPresets
import com.example.annotationmarker.layer.LayerWithGroup
import com.example.annotationmarker.layer.MaplibreLayerManager
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.HeatmapLayer
import org.maplibre.android.style.layers.HillshadeLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection

open class MapSourceManager(
    private val style: Style?
) {
    var maplibreLayerManager: MaplibreLayerManager = MaplibreLayerManager(style)

    /**
     * Хранит список слоёв, связанных с каждым sourceId.
     */
    private val sourceLayers = mutableMapOf<String, List<LayerWithGroup>>()

    fun getExistingSourceLayers() = sourceLayers.keys

    /**
     * Создаёт новый GeoJsonSource и соответствующие слои, либо очищает существующий.
     *
     * @param sourceId Уникальный идентификатор источника.
     * @param preset Функция, возвращающая список слоёв для источника.
     */
    fun buildSource(
        sourceId: String = "defaultSource",
        preset: (String) -> List<LayerWithGroup> = LayerPresets::basePreset,
    ) {
        if (style == null || !style.isFullyLoaded) return
        val source = style.getSourceAs<GeoJsonSource>(sourceId)
        if (source == null) {
            val newSource = GeoJsonSource(sourceId, FeatureCollection.fromFeatures(emptyList()))
            style.addSource(newSource)

            val builtLayers = preset(sourceId)
            sourceLayers[sourceId] = builtLayers

            for (layer in builtLayers) {
                val existingLayer = style.getLayer(layer.layer.id)
                if (existingLayer == null) {
                    maplibreLayerManager.addLayerToGroup(layer.layer, layer.layerGroup)
                }
            }
        } else {
            source.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
        }
    }

    /**
     * Обновляет GeoJSON-данные существующего источника.
     *
     * @param sourceId Идентификатор источника.
     * @param newFeatures Новый список фич для отображения.
     */
    fun updateData(sourceId: String = "defaultSource", newFeatures: List<Feature>) {
        if (style == null || !style.isFullyLoaded) return
        val existingSource = style.getSource(sourceId)
        if (existingSource == null) {
            val geoJsonSource = GeoJsonSource(sourceId, FeatureCollection.fromFeatures(newFeatures))
            style.addSource(geoJsonSource)
        } else if (existingSource is GeoJsonSource) {
            existingSource.setGeoJson(FeatureCollection.fromFeatures(newFeatures))
        }
    }


    /**
     * Очищает данные GeoJson-источника (удаляет все фичи).
     * Сам источник  и слои остается в памяти , использовать только тогда, когда продолжаем работу с данными но переключаемся на другие
     * (Пример: работа с временным рисованием, можно аналогично вызвать updateData с пустым списком)
     * @param sourceId Идентификатор источника.
     */
    fun clearData(sourceId: String = "defaultSource") {
        if (style == null || !style.isFullyLoaded) return
        style.getSourceAs<GeoJsonSource>(sourceId)
            ?.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
    }

    fun getFeaturesByPropertyValue(sourceId: String, key: String, value: String): List<Feature> {
        if (style == null || !style.isFullyLoaded) return emptyList()

        return style.getSourceAs<GeoJsonSource>(sourceId)
            ?.querySourceFeatures(null)
            ?.filter { it.getStringProperty(key) == value }
            ?: emptyList()
    }

    fun getFeaturesByPropertyValues(
        sourceId: String,
        key: String,
        values: List<String>
    ): List<Feature> {
        if (style == null || !style.isFullyLoaded) return emptyList()
        return style.getSourceAs<GeoJsonSource>(sourceId)
            ?.querySourceFeatures(null)
            ?.filter { values.any { value -> value == it.getStringProperty(key) } }
            ?: emptyList()
    }

    fun getFeaturesFromSource(sourceId: String): List<Feature> {
        if (style == null || !style.isFullyLoaded) return emptyList()
        return style.getSourceAs<GeoJsonSource>(sourceId)
            ?.querySourceFeatures(null)
            ?.map { it }
            ?: emptyList()
    }

    fun getFeaturesWithoutPropertyValue(
        sourceId: String,
        key: String,
        value: String
    ): List<Feature> {
        if (style == null || !style.isFullyLoaded) return emptyList()
        return style.getSourceAs<GeoJsonSource>(sourceId)
            ?.querySourceFeatures(null)
            ?.filter { it.getStringProperty(key) != value }
            ?: emptyList()
    }

    fun getFeaturesWithoutPropertyValues(
        sourceId: String,
        key: String,
        values: List<String>
    ): List<Feature> {
        if (style == null || !style.isFullyLoaded) return emptyList()
        return style.getSourceAs<GeoJsonSource>(sourceId)
            ?.querySourceFeatures(null)
            ?.filter { it.getStringProperty(key) !in values }
            ?: emptyList()
    }

    /**
     * Удаляет указанный источник и все связанные с ним слои. Использовать когда заканчиваем работу с данными,
     * чтобы неиспользуемые источники и слои не висели на карте
     *
     * @param sourceId Идентификатор источника.
     */
    fun removeSourceAndLayers(sourceId: String) {
        if (style == null || !style.isFullyLoaded) return

        val layers = sourceLayers[sourceId] ?: emptyList()
        for (layer in layers) {
            style.removeLayer(layer.layer.id)
        }
        sourceLayers.remove(sourceId)
        style.removeSource(sourceId)
    }

    fun logSourcesAndLayers(style: Style) {
        val sb = StringBuilder()
        sb.appendLine("=== MapLibre Sources & Layers ===")

        style.sources.forEach { source ->
            sb.appendLine("Source: id='${source.id}', type=${source.javaClass.simpleName}")

            // Получаем слои, у которых sourceId совпадает
            val layersForSource = style.layers.filter { layer ->
                when (layer) {
                    is FillLayer -> layer.sourceId == source.id
                    is LineLayer -> layer.sourceId == source.id
                    is SymbolLayer -> layer.sourceId == source.id
                    is CircleLayer -> layer.sourceId == source.id
                    is RasterLayer -> layer.sourceId == source.id
                    is HillshadeLayer -> layer.sourceId == source.id
                    is HeatmapLayer -> layer.sourceId == source.id
                    else -> false // Если слой нестандартный — пропускаем
                }
            }

            if (layersForSource.isEmpty()) {
                sb.appendLine("  (no layers)")
            } else {
                layersForSource.forEach { layer ->
                    sb.appendLine("  Layer: id='${layer.id}', type=${layer.javaClass.simpleName}")
                }
            }
        }

        sb.appendLine("=== End of list ===")
        Log.d("MapLibreDebugSource", sb.toString())
    }


}