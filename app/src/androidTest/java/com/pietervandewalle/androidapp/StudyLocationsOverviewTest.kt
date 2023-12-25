package com.pietervandewalle.androidapp

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.pietervandewalle.androidapp.ui.AndroidApp
import com.pietervandewalle.androidapp.ui.navigation.Screens
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StudyLocationsOverviewTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController
    private lateinit var context: Context

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            context = LocalContext.current
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            AndroidAppTheme {
                AndroidApp(navController = navController, startDestination = Screens.StudyLocations.route) // Start at studyLocations
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `search functionality works correctly`() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("studyLocationListItem"), 5000L) // wait for articles to load

        val searchTerm = "MeErs"

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.search_icon)).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed().performTextInput(searchTerm)
        composeTestRule.onNodeWithTag("searchBar").performImeAction() // submit search

        composeTestRule.onNodeWithText(searchTerm, substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("searchResult").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `removing searchTerm clears searchResult`() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("studyLocationListItem"), 5000L) // wait for articles to load

        val searchTerm = "MeErs"

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.search_icon))
            .assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed().performTextInput(searchTerm)
        composeTestRule.onNodeWithTag("searchBar").performImeAction() // submit search

        composeTestRule.onNodeWithText(searchTerm, substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("searchResult").assertIsDisplayed()

        composeTestRule.onNodeWithText(context.getString(R.string.remove_searchTerm)).performClick()
        composeTestRule.onNodeWithTag("searchResult").assertDoesNotExist()
    }
}
