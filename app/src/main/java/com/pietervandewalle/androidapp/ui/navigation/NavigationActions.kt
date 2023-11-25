package com.pietervandewalle.androidapp.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import java.net.URLEncoder

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
        navigateToMenuItem(Screens.BicycleParking.route)
    }

    fun navigateToCarParksOverview() {
        navigateToMenuItem(Screens.CarParking.route)
    }

    fun navigateToArticleDetail(title: String) {
        navController.navigate(
            Screens.ArticleDetail.route.replace(
                DestinationsArgs.ARTICLE_TITLE_ARG,
                URLEncoder.encode(title, "UTF-8"),
            ).replace("{", "").replace("}", ""),
        )
    }

    fun navigateToCarParkDetail(carParkName: String) {
        navController.navigate(
            Screens.CarParkDetail.route.replace(
                DestinationsArgs.CARPARK_NAME_ARG,
                URLEncoder.encode(carParkName, "UTF-8"),
            ).replace("{", "").replace("}", ""),
        )
    }
}
