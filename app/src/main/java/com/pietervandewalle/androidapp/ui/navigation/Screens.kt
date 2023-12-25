package com.pietervandewalle.androidapp.ui.navigation

import androidx.annotation.StringRes
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.ARTICLE_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.CARPARK_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.STUDYLOCATION_ID_ARG

val deepLinkUri = "https://androidapp.be"

object DestinationsArgs {
    const val ARTICLE_ID_ARG = "articleId"
    const val CARPARK_ID_ARG = "carParkId"
    const val STUDYLOCATION_ID_ARG = "studyLocationId"
}
enum class Screens(val route: String, @StringRes val title: Int) {
    Articles("articles", R.string.home_title),
    ArticleDetail("articles/{$ARTICLE_ID_ARG}", R.string.home_title),
    CarParks("carParks", R.string.car_parking),
    CarParkDetail("carParks/{$CARPARK_ID_ARG}", R.string.car_parking),
    StudyLocations("studyLocations", R.string.studylocations),
    StudyLocationDetail("studyLocations/{$STUDYLOCATION_ID_ARG}", R.string.studylocations),
}
