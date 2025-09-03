package com.example.annotationmarker.layer

import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.BackgroundLayer
import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory

/**
Менеджер MapLibre-слоёв, предоставляющий централизованное управление порядком и структурой слоёв на карте.
Создает якорные пустые слои каждого типа из LayerGroup.
 **/
class MaplibreLayerManager(
    private val style: Style?
) {
    var anchorsInitialized = false

    fun attachAnchorLayers() {
        if (style == null) return
        if (anchorsInitialized) return

        MaplibreLayerGroups.entries.forEach { group ->
            if (style.getLayer(group.layerId) == null) {
                style.addLayer(
                    BackgroundLayer(group.layerId).withProperties(
                        PropertyFactory.visibility(
                            Property.NONE
                        )
                    )
                )
            }
        }
        anchorsInitialized = true
    }

    fun addLayerToGroup(
        layer: Layer,
        group: MaplibreLayerGroups,
        replace: Boolean = false,
        indexToReplace: Int? = null
    ) {
        if (style == null) return
        if (!anchorsInitialized)
            attachAnchorLayers()

        // Проверяем, есть ли нужный якорный слой
        style.getLayer(group.layerId) ?: return

        // удаляем слой перед добавлением если уже такой был
        if (style.getLayer(layer.id) != null) {
            style.removeLayer(layer.id)
        }
        if (replace) {
            if (indexToReplace != null) {
                style.addLayerAt(layer, indexToReplace)
            } else {
                style.addLayerBelow(layer, group.layerId)
            }
        } else {
            style.addLayerBelow(layer, group.layerId)
        }
    }

    fun removeLayer(layerId: String, layer: Layer? = null) {
        if (style == null) return
        val targetLayer = layer ?: style.getLayer(layerId)
        if (targetLayer != null) {
            style.removeLayer(targetLayer)
        }
    }

    fun getLayerIndex(layerId: String): Int? {
        if (style == null) return null
        val index = style.layers.indexOfFirst { it.id == layerId }
        return index
    }

}