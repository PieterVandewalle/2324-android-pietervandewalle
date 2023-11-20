package com.pietervandewalle.androidapp.ui.navigation

import androidx.annotation.StringRes
import com.pietervandewalle.androidapp.R

enum class Screens(val route: String, @StringRes val title: Int) {
    Home("home_screen", R.string.home_title),
    Search("search_screen", R.string.search_title),
    Profile("profile_screen", R.string.profile_title),
}
