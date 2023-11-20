package com.pietervandewalle.androidapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.pietervandewalle.androidapp.ui.Screens

@Composable
fun BottomNavigationBar(currentRoute: String?, goHome: () -> Unit, goSearch: () -> Unit, goProfile: () -> Unit) {
    val navigationItems = listOf(
        BottomNavigationItem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = Screens.Home.route,
            onClick = goHome,
        ),
        BottomNavigationItem(
            label = "Search",
            icon = Icons.Filled.Search,
            route = Screens.Search.route,
            onClick = goSearch,
        ),
        BottomNavigationItem(
            label = "Profile",
            icon = Icons.Filled.AccountCircle,
            route = Screens.Profile.route,
            onClick = goProfile,
        ),
    )
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

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String,
    val onClick: () -> Unit,
)
