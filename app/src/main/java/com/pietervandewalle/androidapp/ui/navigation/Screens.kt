package com.pietervandewalle.androidapp.ui.navigation

import androidx.annotation.StringRes
import com.pietervandewalle.androidapp.R

enum class Screens(val route: String, @StringRes val title: Int) {
    Home("home_screen", R.string.home_title),
    CarParking("car_parking_screen", R.string.car_parking),
    BicycleParking("bicycle_parking_screen", R.string.bicycle_parking),
}
