package com.pietervandewalle.androidapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.pietervandewalle.androidapp.ui.articles.detail.ArticleDetailView
import com.pietervandewalle.androidapp.ui.articles.overview.ArticleOverview
import com.pietervandewalle.androidapp.ui.carparks.detail.CarParkDetailView
import com.pietervandewalle.androidapp.ui.carparks.overview.CarParksOverview
import com.pietervandewalle.androidapp.ui.navigation.BottomNavigationBar
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.ARTICLE_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.CARPARK_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.STUDYLOCATION_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.NavigationActions
import com.pietervandewalle.androidapp.ui.navigation.Screens
import com.pietervandewalle.androidapp.ui.navigation.deepLinkUri
import com.pietervandewalle.androidapp.ui.studylocations.detail.StudyLocationDetailView
import com.pietervandewalle.androidapp.ui.studylocations.overview.StudyLocationsOverview

/**
 * Composition local for [SnackbarHostState] used to provide the Snackbar host state
 * to composables within the app.
 */
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No Snackbar Host State provided")
}

/**
 * The main entry point for the Android app.
 *
 * @param navController The navigation controller for managing app navigation.
 * @param navActions Navigation actions for handling common navigation tasks.
 * @param startDestination The starting destination for the navigation (used in tests).
 */
@Composable
fun AndroidApp(
    navController: NavHostController = rememberNavController(),
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
    startDestination: String? = null,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    currentRoute = currentDestination?.route,
                    goHome = navActions::navigateToArticles,
                    goCarParks = navActions::navigateToCarParksOverview,
                    goStudyLocations = navActions::navigateToStudyLocations,
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination ?: Screens.Articles.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(
                    route = Screens.Articles.route,
                ) {
                    ArticleOverview(onNavigateToDetail = navActions::navigateToArticleDetail)
                }
                composable(
                    route = Screens.CarParks.route,
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "$deepLinkUri/${Screens.CarParks.route}" },
                    ),
                ) {
                    CarParksOverview(
                        onNavigateToDetail = navActions::navigateToCarParkDetail,
                    )
                }

                composable(
                    route = Screens.ArticleDetail.route,
                    arguments = listOf(
                        navArgument(
                            ARTICLE_ID_ARG,
                        ) { type = NavType.IntType },
                    ),
                ) {
                    ArticleDetailView(onNavigateBack = navController::popBackStack)
                }

                composable(
                    route = Screens.CarParkDetail.route,
                    arguments = listOf(
                        navArgument(
                            CARPARK_ID_ARG,
                        ) { type = NavType.IntType },
                    ),
                ) {
                    CarParkDetailView(onNavigateBack = navController::popBackStack)
                }

                composable(route = Screens.StudyLocations.route) {
                    StudyLocationsOverview(
                        onNavigateToDetail = navActions::navigateToStudyLocationDetail,
                    )
                }

                composable(
                    route = Screens.StudyLocationDetail.route,
                    arguments = listOf(navArgument(STUDYLOCATION_ID_ARG) { type = NavType.IntType }),
                ) {
                    StudyLocationDetailView(onNavigateBack = navController::popBackStack)
                }
            }
        }
    }
}
