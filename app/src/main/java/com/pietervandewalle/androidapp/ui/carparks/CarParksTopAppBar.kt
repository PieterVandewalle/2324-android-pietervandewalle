package com.pietervandewalle.androidapp.ui.carparks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar

@Composable
fun CarParksTopAppBar(isMapVisible: Boolean, onToggleMap: () -> Unit) {
    MyTopAppBar(screenTitle = R.string.car_parking) {
        IconButton(onClick = onToggleMap) {
            Icon(Icons.Filled.Map, contentDescription = null, tint = if (isMapVisible) MaterialTheme.colorScheme.primary else Color.Black)
        }
    }
}