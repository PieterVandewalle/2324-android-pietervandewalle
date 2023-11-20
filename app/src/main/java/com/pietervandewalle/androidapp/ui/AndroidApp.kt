package com.pietervandewalle.androidapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pietervandewalle.androidapp.ui.articles.ArticleOverview
import com.pietervandewalle.androidapp.ui.navigation.BottomNavigationBar
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.navigation.NavigationActions
import com.pietervandewalle.androidapp.ui.navigation.Screens
import java.nio.file.Files.find

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidApp(
    navController: NavHostController = rememberNavController(),
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreenTitle = Screens.values().find { it.route == navBackStackEntry?.destination?.route ?: Screens.Home.route }!!.title

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = currentScreenTitle)
        },
        bottomBar = {
            BottomNavigationBar(currentRoute = currentDestination?.route, goHome = navActions::navigateToHome, goSearch = navActions::navigateToSearch, goProfile = navActions::navigateToProfile)
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(route = Screens.Home.route) {
                ArticleOverview()
            }
            composable(route = Screens.Search.route) {
                Text(text = "search")
            }
            composable(route = Screens.Profile.route) {
                Text(text = "profile")
            }
        }
    }
}
