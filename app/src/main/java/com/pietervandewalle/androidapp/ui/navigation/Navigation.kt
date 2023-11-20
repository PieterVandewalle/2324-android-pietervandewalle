package com.pietervandewalle.androidapp.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavigationActions(private val navController: NavHostController) {
    private fun navigateToMenuItem(route: String) {
        navController.navigate(route) {
            // Pop up to the start destination of the graph to avoid building up a large stack of destinations
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    fun navigateToHome() {
        navigateToMenuItem(Screens.Home.route)
    }

    fun navigateToProfile() {
        navigateToMenuItem(Screens.Profile.route)
    }

    fun navigateToSearch() {
        navigateToMenuItem(Screens.Search.route)
    }
}
