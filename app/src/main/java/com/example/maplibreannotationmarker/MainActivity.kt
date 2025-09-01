package com.example.maplibreannotationmarker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.maplibreannotationmarker.presentation.main.AppScreen
import org.maplibre.android.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // лучше инициализировать SDK тут или в App иначе может упасть
        MapLibre.getInstance(this)
        MapLibre.setConnected(true)

        setContent {
            AppScreen()
        }
    }
}