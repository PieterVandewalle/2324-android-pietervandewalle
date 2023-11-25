package com.pietervandewalle.androidapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun BottomNavigationBar(currentRoute: String?, goHome: () -> Unit, goCarParks: () -> Unit, goProfile: () -> Unit) {
    val navigationItems = listOf(
        BottomNavigationItem(
            label = stringResource(Screens.Home.title),
            icon = Icons.Filled.Home,
            route = Screens.Home.route,
            onClick = goHome,
        ),
        BottomNavigationItem(
            label = stringResource(Screens.CarParking.title),
            icon = Icons.Filled.Garage,
            route = Screens.CarParking.route,
            onClick = goCarParks,
        ),
        BottomNavigationItem(
            label = stringResource(Screens.BicycleParking.title),
            icon = Icons.Filled.PedalBike,
            route = Screens.BicycleParking.route,
            onClick = goProfile,
        ),
    )
    if (navigationItems.any { navItem -> navItem.route == currentRoute }) {
        NavigationBar {
            navigationItems.forEachIndexed { _, navigationItem ->
                NavigationBarItem(
                    selected = navigationItem.route == currentRoute,
                    label = {
                        Text(navigationItem.label)
                    },
                    icon = {
                        Icon(
                            navigationItem.icon,
                            contentDescription = navigationItem.label,
                        )
                    },
                    onClick = {
                        navigationItem.onClick()
                    },
                )
            }
        }
    }
}

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String,
    val onClick: () -> Unit,
)
