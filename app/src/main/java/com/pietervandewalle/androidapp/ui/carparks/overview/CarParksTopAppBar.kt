package com.pietervandewalle.androidapp.ui.carparks.overview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarParksTopAppBar(isMapVisible: Boolean, onToggleMap: () -> Unit) {
    MyTopAppBar(screenTitle = R.string.car_parking) {
        IconButton(onClick = onToggleMap) {
            Icon(if (isMapVisible) Icons.Outlined.Map else Icons.Filled.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
