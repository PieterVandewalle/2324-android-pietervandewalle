package com.pietervandewalle.androidapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@Composable
fun BottomNavigationBar(currentRoute: String?, goHome: () -> Unit, goCarParks: () -> Unit, goStudyLocations: () -> Unit) {
    val navigationItems = listOf(
        BottomNavigationItem(
            label = stringResource(Screens.Articles.title),
            icon = Icons.Filled.Home,
            route = Screens.Articles.route,
            onClick = goHome,
        ),
        BottomNavigationItem(
            label = stringResource(Screens.CarParks.title),
            icon = Icons.Filled.Garage,
            route = Screens.CarParks.route,
            onClick = goCarParks,
        ),
        BottomNavigationItem(
            label = stringResource(Screens.StudyLocations.title),
            icon = Icons.Filled.School,
            route = Screens.StudyLocations.route,
            onClick = goStudyLocations,
        ),
    )
    val showNavigationBar = remember(currentRoute) {
        navigationItems.any { it.route == currentRoute }
    }

    if (showNavigationBar) {
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

@Composable
@Preview(showBackground = true)
private fun BottomNavigationBarPreview() {
    AndroidAppTheme {
        BottomNavigationBar(currentRoute = Screens.Articles.route, goHome = { }, goCarParks = { }, goStudyLocations = {})
    }
}
