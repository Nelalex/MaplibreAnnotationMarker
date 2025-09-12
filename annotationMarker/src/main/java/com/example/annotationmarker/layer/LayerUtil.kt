package com.example.annotationmarker.layer

import com.example.annotationmarker.util.Constants.FILL_COLOR_PROPERTY
import com.example.annotationmarker.util.Constants.FILL_OPACITY_PROPERTY
import com.example.annotationmarker.util.Constants.LINE_COLOR_PROPERTY
import com.example.annotationmarker.util.Constants.LINE_WIDTH_PROPERTY
import com.example.annotationmarker.util.Constants.TEXT_COLOR_PROPERTY
import com.example.annotationmarker.util.Constants.TEXT_HALO_COLOR_PROPERTY
import com.example.annotationmarker.util.Constants.TEXT_HALO_WIDTH_PROPERTY
import com.example.annotationmarker.util.Constants.TEXT_PROPERTY
import com.example.annotationmarker.util.Constants.TEXT_SIZE_PROPERTY
import com.example.annotationmarker.util.Constants.UNIQUE_ICON_NAME_PROPERTY
import com.example.annotationmarker.util.Constants.UNIQUE_MARKER_TYPE_PROPERTY
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.expressions.Expression.literal
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.Property.ICON_ANCHOR_CENTER
import org.maplibre.android.style.layers.Property.SYMBOL_PLACEMENT_POINT
import org.maplibre.android.style.layers.Property.TEXT_ROTATION_ALIGNMENT_VIEWPORT
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconAnchor
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.PropertyFactory.iconOptional
import org.maplibre.android.style.layers.PropertyFactory.iconSize
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.layers.PropertyFactory.symbolPlacement
import org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.textAnchor
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textField
import org.maplibre.android.style.layers.PropertyFactory.textFont
import org.maplibre.android.style.layers.PropertyFactory.textHaloColor
import org.maplibre.android.style.layers.PropertyFactory.textHaloWidth
import org.maplibre.android.style.layers.PropertyFactory.textIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.textOffset
import org.maplibre.android.style.layers.PropertyFactory.textOpacity
import org.maplibre.android.style.layers.PropertyFactory.textRotationAlignment
import org.maplibre.android.style.layers.PropertyFactory.textSize
import org.maplibre.android.style.layers.SymbolLayer

object MaplibreLayersUtil {

    fun createMapTargetSymbolLayer(
        sourceId: String,
        layerId: String,
        uniqueType: String? = null
    ): LayerWithGroup {
        val symbolLayer = SymbolLayer(layerId, sourceId).apply {
            if (uniqueType != null) {
                withFilter(
                    Expression.all(
                        Expression.eq(Expression.geometryType(), Expression.literal("Point")),
                        Expression.eq(
                            Expression.get(UNIQUE_MARKER_TYPE_PROPERTY),
                            Expression.literal(uniqueType)
                        )
                    )
                )
            } else withFilter(Expression.eq(Expression.geometryType(), Expression.literal("Point")))

            withProperties(
                iconImage(Expression.get(UNIQUE_ICON_NAME_PROPERTY)),
                iconAnchor(ICON_ANCHOR_CENTER),
                iconAllowOverlap(true),
                iconIgnorePlacement(true),
                textIgnorePlacement(true),
                textAllowOverlap(true),
                textField(Expression.get(TEXT_PROPERTY)),
                textColor(Expression.get(TEXT_COLOR_PROPERTY)),
                textFont(arrayOf("Open Sans Regular", "Arial Unicode MS Regular")),
                textHaloColor(Expression.get(TEXT_HALO_COLOR_PROPERTY)),
                textHaloWidth(Expression.get(TEXT_HALO_WIDTH_PROPERTY)),
                textAnchor(Property.TEXT_ANCHOR_TOP),
                textSize(
                    Expression.interpolate(
                        Expression.linear(),
                        Expression.zoom(),
                        literal(5.0),
                        Expression.division(Expression.get(TEXT_SIZE_PROPERTY), literal(5)),
                        literal(10.0),
                        Expression.division(Expression.get(TEXT_SIZE_PROPERTY), literal(3.5)),
                        literal(15.0),
                        Expression.division(Expression.get(TEXT_SIZE_PROPERTY), literal(2.0)),
                        literal(18.0),
                        Expression.division(
                            Expression.get(TEXT_SIZE_PROPERTY),
                            literal(1.3)
                        ),
                    )
                ),
                iconSize(
                    Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.stop(3, 0.3f),
                        Expression.stop(10, 0.6f),
                        Expression.stop(12, 0.8f),
                        Expression.stop(15, 1f),
                        Expression.stop(25, 1f)
                    )
                ),
                textOffset(
                    Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.stop(10, literal(arrayOf(0.0, 0.5))),
                        Expression.stop(14, literal(arrayOf(0.0, 1))),
                        Expression.stop(18, literal(arrayOf(0.0, 1.5))),
                        Expression.stop(20, literal(arrayOf(0.0, 2.0)))
                    )
                ),
                textOpacity(
                    Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.stop(7, 0),
                        Expression.stop(10, 1)
                    )
                ),
                iconOptional(false)
            )
        }
        return LayerWithGroup(MaplibreLayerGroups.SYMBOL_LAYERS, symbolLayer)
    }

    fun createFillLayer(
        sourceId: String,
        layerId: String,
        uniqueType: String? = null
    ): LayerWithGroup {
        val fillLayer = FillLayer(layerId, sourceId).apply {
            if (uniqueType != null) {
                withFilter(
                    Expression.all(
                        Expression.eq(
                            Expression.get(UNIQUE_MARKER_TYPE_PROPERTY),
                            Expression.literal(uniqueType)
                        ),
                        Expression.eq(Expression.geometryType(), Expression.literal("Polygon"))
                    )
                )
            } else withFilter(
                Expression.eq(
                    Expression.geometryType(),
                    Expression.literal("Polygon")
                )
            )

            withProperties(
                fillColor(
                    Expression.coalesce(
                        Expression.get(FILL_COLOR_PROPERTY),
                        Expression.literal("#000000") // Значение по умолчанию
                    )
                ),
                fillOpacity(
                    Expression.coalesce(
                        Expression.get(FILL_OPACITY_PROPERTY),
                        Expression.literal(0.0f) // Значение по умолчанию
                    )
                )
            )
        }
        return LayerWithGroup(MaplibreLayerGroups.FILL_LAYERS, fillLayer)
    }

    fun createLineLayer(
        sourceId: String,
        layerId: String,
        uniqueType: String? = null
    ): LayerWithGroup {
        val lineLayer = LineLayer(layerId, sourceId).apply {
            if (uniqueType != null) {
                withFilter(
                    Expression.all(
                        Expression.eq(
                            Expression.get(UNIQUE_MARKER_TYPE_PROPERTY),
                            Expression.literal(uniqueType),
                            Expression.eq(
                                Expression.geometryType(),
                                Expression.literal("LineString")
                            )
                        )
                    )

                )
            } else withFilter(
                Expression.eq(
                    Expression.geometryType(),
                    Expression.literal("LineString")
                )
            )

            withProperties(
                lineColor(
                    Expression.coalesce(
                        Expression.get(LINE_COLOR_PROPERTY),
                        Expression.literal("#000000")
                    )
                ),
                lineWidth(
                    Expression.coalesce(
                        Expression.get(LINE_WIDTH_PROPERTY),
                        Expression.literal(2.0f)
                    )
                )
            )
        }
        return LayerWithGroup(MaplibreLayerGroups.LINE_LAYERS, lineLayer)
    }

    fun createTextLayer(
        sourceId: String,
        layerId: String,
        uniqueType: String? = null
    ): LayerWithGroup {
        val textLayer = SymbolLayer(layerId, sourceId).apply {
            if (uniqueType != null) {
                withFilter(
                    Expression.all(
                        Expression.eq(Expression.geometryType(), Expression.literal("Point")),
                        Expression.eq(
                            Expression.get(UNIQUE_MARKER_TYPE_PROPERTY),
                            Expression.literal(uniqueType)
                        )
                    )
                )
            } else withFilter(Expression.eq(Expression.geometryType(), Expression.literal("Point")))

            withProperties(
                textField(Expression.get(TEXT_PROPERTY)),
                textColor(Expression.get(TEXT_COLOR_PROPERTY)),
                textFont(arrayOf("Open Sans Regular", "Arial Unicode MS Regular")),
                textHaloColor(Expression.get(TEXT_HALO_COLOR_PROPERTY)),
                textHaloWidth(Expression.get(TEXT_HALO_WIDTH_PROPERTY)),
                textSize(
                    Expression.interpolate(
                        Expression.linear(),
                        Expression.zoom(),
                        literal(5.0),
                        Expression.division(Expression.get(TEXT_SIZE_PROPERTY), literal(5)),
                        literal(10.0),
                        Expression.division(Expression.get(TEXT_SIZE_PROPERTY), literal(3.5)),
                        literal(15.0),
                        Expression.division(Expression.get(TEXT_SIZE_PROPERTY), literal(2.0)),
                        literal(18.0),
                        Expression.division(
                            Expression.get(TEXT_SIZE_PROPERTY),
                            literal(1.3)
                        ),
                    )
                ),
                textAllowOverlap(true),
                textIgnorePlacement(true),
                symbolPlacement(SYMBOL_PLACEMENT_POINT),
                textRotationAlignment(TEXT_ROTATION_ALIGNMENT_VIEWPORT)
            )
        }
        return LayerWithGroup(MaplibreLayerGroups.SYMBOL_NO_ICON_LAYERS, textLayer)
    }

}