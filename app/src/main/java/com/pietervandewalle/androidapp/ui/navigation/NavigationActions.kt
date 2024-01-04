package com.pietervandewalle.androidapp.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.ARTICLE_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.CARPARK_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.STUDYLOCATION_ID_ARG

/**
 * A helper class for managing navigation actions within the app.
 *
 * @param navController The [NavHostController] used for navigation.
 */
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

    /**
     * Navigate to the home screen.
     */
    fun navigateToArticles() {
        navigateToMenuItem(Screens.Articles.route)
    }

    /**
     * Navigate to the study locations screen.
     */
    fun navigateToStudyLocations() {
        navigateToMenuItem(Screens.StudyLocations.route)
    }

    /**
     * Navigate to the car parks overview screen.
     */
    fun navigateToCarParksOverview() {
        navigateToMenuItem(Screens.CarParks.route)
    }

    /**
     * Navigate to the article detail screen with the specified article ID.
     *
     * @param articleId The ID of the article to display.
     */
    fun navigateToArticleDetail(articleId: Int) {
        navController.navigate(
            Screens.ArticleDetail.route.replace(
                "{$ARTICLE_ID_ARG}",
                articleId.toString(),
            ),
        )
    }

    /**
     * Navigate to the car park detail screen with the specified car park ID.
     *
     * @param carParkId The ID of the car park to display.
     */
    fun navigateToCarParkDetail(carParkId: Int) {
        navController.navigate(
            Screens.CarParkDetail.route.replace(
                "{$CARPARK_ID_ARG}",
                carParkId.toString(),
            ),
        )
    }

    /**
     * Navigate to the study location detail screen with the specified study location ID.
     *
     * @param studyLocationId The ID of the study location to display.
     */
    fun navigateToStudyLocationDetail(studyLocationId: Int) {
        navController.navigate(
            Screens.StudyLocationDetail.route.replace(
                "{$STUDYLOCATION_ID_ARG}",
                studyLocationId.toString(),
            ),
        )
    }
}
