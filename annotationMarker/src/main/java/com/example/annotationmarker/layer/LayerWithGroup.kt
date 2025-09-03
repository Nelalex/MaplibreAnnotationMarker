package com.example.annotationmarker.layer

import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.HeatmapLayer
import org.maplibre.android.style.layers.HillshadeLayer
import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.layers.SymbolLayer

data class LayerWithGroup(
    val layerGroup: MaplibreLayerGroups,
    val layer: Layer
)

private fun LayerWithGroup.withGroup(group: String): LayerWithGroup {
    return LayerWithGroup(layerGroup, layer)
}

fun Layer.toLayerWithGroup(): LayerWithGroup {
    val group = when (this) {
        is FillLayer -> MaplibreLayerGroups.FILL_LAYERS
        is LineLayer -> MaplibreLayerGroups.LINE_LAYERS
        is SymbolLayer -> {
            // Если у символа нет иконки (только текст), то кладём в SYMBOL_NO_ICON_LAYERS
            val hasIconImage = iconImage.value != null
            if (hasIconImage) MaplibreLayerGroups.SYMBOL_LAYERS
            else MaplibreLayerGroups.SYMBOL_NO_ICON_LAYERS
        }

        is CircleLayer -> MaplibreLayerGroups.FILL_LAYERS
        is RasterLayer,
        is HillshadeLayer,
        is HeatmapLayer -> MaplibreLayerGroups.BACKGROUND

        else -> MaplibreLayerGroups.INFO_WINDOWS
    }
    return LayerWithGroup(group, this)
}
