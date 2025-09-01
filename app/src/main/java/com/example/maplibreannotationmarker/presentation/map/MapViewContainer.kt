package com.example.maplibreannotationmarker.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.maplibreannotationmarker.R
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun MapViewContainer() {
    val context = LocalContext.current
    // Для апишки которая дает тайлы, можно потом сделать под локальную
    val apiKey = context.getString(R.string.map_style_key)

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                getMapAsync { map ->
                    map.setStyle(
                        Style.Builder().fromUri(
                            "https://tiles.stadiamaps.com/styles/alidade_smooth_dark.json?api_key=$apiKey"
                        )
                    )
                    map.uiSettings.isLogoEnabled = false
                    map.uiSettings.isAttributionEnabled = false
                }

                // Привязка жизненного цикла
                lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        when (event) {
                            Lifecycle.Event.ON_START -> onStart()
                            Lifecycle.Event.ON_RESUME -> onResume()
                            Lifecycle.Event.ON_PAUSE -> onPause()
                            Lifecycle.Event.ON_STOP -> onStop()
                            Lifecycle.Event.ON_DESTROY -> onDestroy()
                            else -> {}
                        }
                    }
                })
            }
        }
    )
}
