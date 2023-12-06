package com.pietervandewalle.androidapp.ui.navigation

import androidx.annotation.StringRes
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.ARTICLE_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.CARPARK_NAME_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.STUDYLOCATION_ID_ARG

object DestinationsArgs {
    const val ARTICLE_ID_ARG = "articleTitle"
    const val CARPARK_NAME_ARG = "carParkName"
    const val STUDYLOCATION_ID_ARG = "studyLocationId"
}
enum class Screens(val route: String, @StringRes val title: Int) {
    Home("home_screen", R.string.home_title),
    ArticleDetail("articles/{$ARTICLE_ID_ARG}", R.string.home_title),
    CarParking("car_parking_screen", R.string.car_parking),
    CarParkDetail("carParks/{$CARPARK_NAME_ARG}", R.string.car_parking),
    StudyLocations("studylocations", R.string.studylocations),
    StudyLocationDetail("studylocations/{$STUDYLOCATION_ID_ARG}", R.string.studylocations),
}
