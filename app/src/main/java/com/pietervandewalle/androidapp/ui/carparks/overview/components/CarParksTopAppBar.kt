package com.pietervandewalle.androidapp.ui.carparks.overview.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

/**
 * Composable function for displaying the top app bar in the car parks overview screen.
 *
 * @param isMapVisible A boolean indicating whether the map view is currently visible.
 * @param onToggleMap A lambda that will be invoked when the map view toggle button is clicked.
 */
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


@Preview(showBackground = true)
@Composable
private fun CarParksTopAppBarPreview() {
    AndroidAppTheme {
        CarParksTopAppBar(isMapVisible = false, onToggleMap = {})
    }
}