package com.pietervandewalle.androidapp.ui.navigation

sealed class Screens(val route: String) {
    object Home : Screens("home_screen")
    object Search : Screens("search_screen")
    object Profile : Screens("profile_screen")
}
