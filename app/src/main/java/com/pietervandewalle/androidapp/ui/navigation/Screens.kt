package com.pietervandewalle.androidapp.ui.navigation

import androidx.annotation.StringRes
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.ARTICLE_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.CARPARK_ID_ARG
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.STUDYLOCATION_ID_ARG

/**
 * The deep link URI for the Android app.
 */
const val deepLinkUri = "https://androidapp.be"

/**
 * Object containing argument keys for destination screens.
 */
object DestinationsArgs {
    /**
     * Argument key for article ID.
     */
    const val ARTICLE_ID_ARG = "articleId"

    /**
     * Argument key for car park ID.
     */
    const val CARPARK_ID_ARG = "carParkId"

    /**
     * Argument key for study location ID.
     */
    const val STUDYLOCATION_ID_ARG = "studyLocationId"
}

/**
 * Enum representing various screens in the app with their routes and titles.
 *
 * @param route The route for the screen.
 * @param title The title resource ID for the screen.
 */
enum class Screens(val route: String, @StringRes val title: Int) {
    /**
     * Represents the "Articles" screen.
     */
    Articles("articles", R.string.home_title),

    /**
     * Represents the "ArticleDetail" screen.
     */
    ArticleDetail("articles/{$ARTICLE_ID_ARG}", R.string.home_title),

    /**
     * Represents the "CarParks" screen.
     */
    CarParks("carParks", R.string.car_parking),

    /**
     * Represents the "CarParkDetail" screen.
     */
    CarParkDetail("carParks/{$CARPARK_ID_ARG}", R.string.car_parking),

    /**
     * Represents the "StudyLocations" screen.
     */
    StudyLocations("studyLocations", R.string.studylocations),

    /**
     * Represents the "StudyLocationDetail" screen.
     */
    StudyLocationDetail("studyLocations/{$STUDYLOCATION_ID_ARG}", R.string.studylocations),
}
