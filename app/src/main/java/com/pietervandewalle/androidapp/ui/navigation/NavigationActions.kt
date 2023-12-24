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

    fun navigateToStudyLocations() {
        navigateToMenuItem(Screens.StudyLocations.route)
    }

    fun navigateToCarParksOverview() {
        navigateToMenuItem(Screens.CarParks.route)
    }

    fun navigateToArticleDetail(articleId: Int) {
        navController.navigate(
            Screens.ArticleDetail.route.replace(
                DestinationsArgs.ARTICLE_ID_ARG,
                articleId.toString(),
            ).replace("{", "").replace("}", ""),
        )
    }

    fun navigateToCarParkDetail(carParkId: Int) {
        navController.navigate(
            Screens.CarParkDetail.route.replace(
                DestinationsArgs.CARPARK_ID_ARG,
                carParkId.toString(),
            ).replace("{", "").replace("}", ""),
        )
    }

    fun navigateToStudyLocationDetail(studyLocationId: Int) {
        navController.navigate(
            Screens.StudyLocationDetail.route.replace(
                DestinationsArgs.STUDYLOCATION_ID_ARG,
                studyLocationId.toString(),
            ).replace("{", "").replace("}", ""),
        )
    }
}
