package com.example.annotationmarker.layer

import com.example.annotationmarker.util.Constants

object LayerPresets {

    // Базовый пресет слоев для корректной визуализации фичей для любого источника
    fun basePreset(sourceId: String): List<LayerWithGroup> {
        return listOf(
            MaplibreLayersUtil.createFillLayer(
                sourceId,
                "${sourceId}${Constants.FILL_LAYER_SUFFIX}"
            ),
            MaplibreLayersUtil.createLineLayer(
                sourceId,
                "${sourceId}${Constants.LINE_LAYER_SUFFIX}"
            ),
            MaplibreLayersUtil.createMapTargetSymbolLayer(
                sourceId,
                "${sourceId}${Constants.SYMBOL_LAYER_SUFFIX}"
            ),
            MaplibreLayersUtil.createTextLayer(
                sourceId,
                "${sourceId}${Constants.SYMBOL_NO_ICON_LAYER_SUFFIX}"
            )
        )
    }
}