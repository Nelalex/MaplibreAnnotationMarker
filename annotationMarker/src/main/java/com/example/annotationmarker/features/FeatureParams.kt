package com.example.annotationmarker.features

import android.graphics.Color
import com.example.annotationmarker.util.Extensions.toHexColor

sealed class FeatureParams(
    open val objectId: Int,
    open val uniqueType: String,
    open val clickable: Boolean,
) {
    companion object {
        const val DEFAULT_TEXT_COLOR = Color.RED
        const val DEFAULT_TEXT_SIZE = 17f
        const val DEFAULT_HALO_COLOR = Color.WHITE
        const val DEFAULT_HALO_WIDTH = 0.5f
        const val DEFAULT_ICON_SIZE = 17f
        const val DEFAULT_LINE_COLOR = Color.RED
        const val DEFAULT_LINE_WIDTH = 1.5f
        const val DEFAULT_FILL_COLOR = Color.RED
        const val DEFAULT_FILL_OPACITY = 0.3f
        const val DEFAULT_CLICKABLE_OPTION = false
        const val DEFAULT_IS_OUTLINE_LINE = false
    }

    data class IconParams(
        val iconSize: Float = DEFAULT_ICON_SIZE,
        override val objectId: Int,
        override val uniqueType: String,
        override val clickable: Boolean = DEFAULT_CLICKABLE_OPTION,
    ) : FeatureParams(objectId, uniqueType, clickable)

    data class TextParams(
        val textColor: String = DEFAULT_TEXT_COLOR.toHexColor(),
        val textSize: Float = DEFAULT_TEXT_SIZE,
        val textHaloColor: String = DEFAULT_HALO_COLOR.toHexColor(),
        val textHaloWidth: Float = DEFAULT_HALO_WIDTH,
        override val objectId: Int,
        override val uniqueType: String,
        override val clickable: Boolean = DEFAULT_CLICKABLE_OPTION,
    ) : FeatureParams(objectId, uniqueType, clickable)

    data class LineParams(
        val lineColor: String = DEFAULT_LINE_COLOR.toHexColor(),
        val lineWidth: Float = DEFAULT_LINE_WIDTH,
        val isOutlineLine: Boolean = DEFAULT_IS_OUTLINE_LINE,
        override val objectId: Int,
        override val uniqueType: String,
        override val clickable: Boolean = DEFAULT_CLICKABLE_OPTION,
    ) : FeatureParams(objectId, uniqueType, clickable)

    data class FillParams(
        val fillColor: String = DEFAULT_FILL_COLOR.toHexColor(),
        val fillOpacity: Float = DEFAULT_FILL_OPACITY,
        val lineColor: String = DEFAULT_LINE_COLOR.toHexColor(),
        val lineWidth: Float = DEFAULT_LINE_WIDTH,
        override val objectId: Int,
        override val uniqueType: String,
        override val clickable: Boolean = DEFAULT_CLICKABLE_OPTION,
    ) : FeatureParams(objectId, uniqueType, clickable)
}