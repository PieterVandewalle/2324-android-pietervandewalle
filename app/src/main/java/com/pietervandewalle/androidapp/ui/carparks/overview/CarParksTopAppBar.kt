package com.pietervandewalle.androidapp.ui.carparks.overview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar

@Composable
fun CarParksTopAppBar(isMapVisible: Boolean, onToggleMap: () -> Unit) {
    MyTopAppBar(screenTitle = R.string.car_parking) {
        IconButton(onClick = onToggleMap) {
            Icon(
                if (isMapVisible) Icons.Outlined.Map else Icons.Filled.Map,
                contentDescription = stringResource(
                    R.string.open_map_view,
                ),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
