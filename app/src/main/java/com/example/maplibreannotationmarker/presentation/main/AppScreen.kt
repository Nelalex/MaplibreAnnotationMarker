package com.example.maplibreannotationmarker.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.annotationmarker.AnnotationMarkerManager
import com.example.annotationmarker.features.FeatureParams
import com.example.annotationmarker.features.MaplibreObjectsUtil
import com.example.annotationmarker.util.Extensions.toHexColor
import com.example.maplibreannotationmarker.presentation.map.MapViewContainer
import org.maplibre.android.geometry.LatLng

@Composable
fun AppScreen() {
    var manager by remember { mutableStateOf<AnnotationMarkerManager?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        MapViewContainer(
            onMapInitialized = { mapManager ->
                manager = mapManager
            }
        )
        IconButton(
            onClick = {
                val x = MaplibreObjectsUtil.createLineFeature(
                    points = listOf(LatLng(55.5740, 37.3193), LatLng(55.9170, 37.9457)),
                    params = FeatureParams.LineParams(
                        lineColor = android.graphics.Color.GREEN.toHexColor(),
                        objectId = 1,
                        uniqueType = "line"
                    )
                )
                manager?.mapSourceManager?.buildSource()
                manager?.addToSource(features = listOf(x))
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red),
            modifier = Modifier.padding(top = 50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(25.dp)
            )
        }
    }
}
