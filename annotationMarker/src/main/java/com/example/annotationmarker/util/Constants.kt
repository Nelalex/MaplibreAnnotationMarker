package com.example.annotationmarker.util

object Constants {

    // уникальные названия "якорных слоев"
    const val BACKGROUND_LAYER_NAME = "group_background"
    const val FILL_LAYER_NAME = "group_fillLayer"
    const val LINE_LAYER_NAME = "group_lineLayer"
    const val SYMBOL_LAYER_NAME = "group_symbolLayer"
    const val SYMBOL_NO_ICON_LAYER_NAME = "group_labelLayer"
    const val INFO_WINDOW_LAYER = "group_infoWindow"

    // cуффиксы для слоев пользователя
    const val FILL_LAYER_SUFFIX = "_fill"
    const val LINE_LAYER_SUFFIX = "_line"
    const val SYMBOL_LAYER_SUFFIX = "_symbol"
    const val SYMBOL_NO_ICON_LAYER_SUFFIX = "_text"
    // Константы для визуализации слоев по доп свойствам


    const val UNIQUE_MARKER_TYPE_PROPERTY =
        "unique_marker_type" // для определения групп меток с одинаковой визуализацией
    const val UNIQUE_MARKER_ID_PROPERTY =
        "unique_marker_type_id" // уникальный идентификатор каждого маркера определенного типа
    const val UNIQUE_ICON_NAME_PROPERTY = "icon" // уникальный идентификатор иконки в стиле карты
    const val ICON_SIZE_PROPERTY = "icon_size" // размер иконки
    const val TEXT_PROPERTY = "text" // текстовая подпись
    const val TEXT_COLOR_PROPERTY = "text_color" // цвет текста
    const val CLICKABLE_PROPERTY = "clickable" // кликабельность
    const val TEXT_SIZE_PROPERTY = "text_size" // размер текста
    const val TEXT_HALO_COLOR_PROPERTY = "text_halo_color" // обводка текста
    const val TEXT_HALO_WIDTH_PROPERTY = "text_halo_width" // цвет обводки текста

    const val FILL_COLOR_PROPERTY = "fill_color" // цвет заливки
    const val FILL_OPACITY_PROPERTY = "fill_opacity" // прозрачность: значения 0.0f - 1.0f

    const val LINE_COLOR_PROPERTY = "line_color" // цвет линии
    const val LINE_WIDTH_PROPERTY = "line_width" // толщина линии


}