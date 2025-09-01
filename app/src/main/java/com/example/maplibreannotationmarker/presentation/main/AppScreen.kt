package com.example.maplibreannotationmarker.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.maplibreannotationmarker.presentation.map.MapViewContainer

@Composable
fun AppScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Android Вьюха под карту
        MapViewContainer()

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

        }
    }
}