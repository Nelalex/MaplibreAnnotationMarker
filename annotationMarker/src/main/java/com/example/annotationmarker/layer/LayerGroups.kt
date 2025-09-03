package com.example.annotationmarker.layer

import com.example.annotationmarker.util.Constants.BACKGROUND_LAYER_NAME
import com.example.annotationmarker.util.Constants.FILL_LAYER_NAME
import com.example.annotationmarker.util.Constants.INFO_WINDOW_LAYER
import com.example.annotationmarker.util.Constants.LINE_LAYER_NAME
import com.example.annotationmarker.util.Constants.SYMBOL_LAYER_NAME
import com.example.annotationmarker.util.Constants.SYMBOL_NO_ICON_LAYER_NAME

/**
 * Перечисление логических "групп слоёв", которые задают порядок визуализации на карте.
 * Используется пустые(якорные) слои для управления порядком и позиционированием всех типов MapLibre-слоёв.
 */
enum class MaplibreLayerGroups(val layerId: String) {

    /**
     * Базовый фон — нижний слой карты.
     * может быть пустым.
     */
    BACKGROUND(BACKGROUND_LAYER_NAME),

    /**
     * Площадные объекты (полигоны).
     */
    FILL_LAYERS(FILL_LAYER_NAME),

    /**
     * Линейные объекты.
     */
    LINE_LAYERS(LINE_LAYER_NAME),

    /**
     * Символьные объекты — иконки и маркеры.
     */
    SYMBOL_LAYERS(SYMBOL_LAYER_NAME),

    /**
     * Подписи и текстовые элементы.
     */
    SYMBOL_NO_ICON_LAYERS(SYMBOL_NO_ICON_LAYER_NAME),

    /**
     * подсказки, инфоокна и UI-оверлеи.
     * может быть пустым
     */
    INFO_WINDOWS(INFO_WINDOW_LAYER)
}

