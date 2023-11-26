package com.pietervandewalle.androidapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.pietervandewalle.androidapp.ui.articles.detail.ArticleDetailView
import com.pietervandewalle.androidapp.ui.articles.overview.ArticleOverview
import com.pietervandewalle.androidapp.ui.carparks.detail.CarParkDetailView
import com.pietervandewalle.androidapp.ui.carparks.overview.CarParksOverview
import com.pietervandewalle.androidapp.ui.navigation.BottomNavigationBar
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.STUDYLOCATION_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.NavigationActions
import com.pietervandewalle.androidapp.ui.navigation.Screens
import com.pietervandewalle.androidapp.ui.studylocations.StudyLocationsOverview
import com.pietervandewalle.androidapp.ui.studylocations.detail.StudyLocationDetailView

@Composable
fun AndroidApp(
    navController: NavHostController = rememberNavController(),
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = currentDestination?.route, goHome = navActions::navigateToHome, goCarParks = navActions::navigateToCarParksOverview, goStudyLocations = navActions::navigateToStudyLocations)
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(innerPadding),
//            enterTransition = {
//                slideIntoContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Up,
//                    animationSpec = tween(300),
//                )
//            },
//            exitTransition = {
//                slideOutOfContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Down,
//                    animationSpec = tween(300),
//                )
//            },
        ) {
            composable(
                route = Screens.Home.route,
            ) {
                ArticleOverview(onNavigateToDetail = navActions::navigateToArticleDetail)
            }
            composable(route = Screens.CarParking.route) {
                CarParksOverview(
                    onNavigateToDetail = navActions::navigateToCarParkDetail,
                )
            }

            composable(route = Screens.ArticleDetail.route) {
                ArticleDetailView(onNavigateBack = navController::popBackStack)
            }

            composable(route = Screens.CarParkDetail.route) {
                CarParkDetailView(onNavigateBack = navController::popBackStack)
            }

            composable(route = Screens.StudyLocations.route) {
                StudyLocationsOverview(onNavigateToDetail = navActions::navigateToStudyLocationDetail)
            }

            composable(route = Screens.StudyLocationDetail.route, arguments = listOf(navArgument(STUDYLOCATION_ID_ARG) { type = NavType.IntType })) {
                StudyLocationDetailView(onNavigateBack = navController::popBackStack)
            }
        }
    }
}
