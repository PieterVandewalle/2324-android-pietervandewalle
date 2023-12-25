package com.pietervandewalle.androidapp

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.pietervandewalle.androidapp.ui.AndroidApp
import com.pietervandewalle.androidapp.ui.navigation.Screens
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController
    private lateinit var context: Context

    private fun setupAppNavHost(startDestination: String? = null) {
        composeTestRule.setContent {
            context = LocalContext.current
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            AndroidAppTheme {
                AndroidApp(navController = navController, startDestination = startDestination)
            }
        }
    }

    @Test
    fun `startDestination is articles overview`() {
        setupAppNavHost()
        composeTestRule.onNode(hasClickAction().and(hasText(context.getString(Screens.CarParks.title)))).assertIsDisplayed()

        composeTestRule.onNode(hasTestTag("topAppBarTitle").and(hasText(context.getString(Screens.Articles.title)))).assertIsDisplayed()
    }

    @Test
    fun `navigation to carParks displays correct screen`() {
        setupAppNavHost()
        composeTestRule
            .onNodeWithText(context.getString(Screens.CarParks.title))
            .performClick()

        composeTestRule.onNode(hasTestTag("topAppBarTitle").and(hasText(context.getString(Screens.CarParks.title)))).assertIsDisplayed()
    }

    @Test
    fun `navigation to studyLocations displays correct screen`() {
        setupAppNavHost()
        composeTestRule
            .onNodeWithText(context.getString(Screens.StudyLocations.title))
            .performClick()

        composeTestRule.onNode(hasTestTag("topAppBarTitle").and(hasText(context.getString(Screens.StudyLocations.title)))).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `navigation to articleDetail displays correct content`() {
        setupAppNavHost(Screens.Articles.route) // Start at Articles overview to make sure this test does not depend on the result of other navigation tests
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("articleListItem"), 5000L) // wait for articles to load

        val articleTitle = composeTestRule.onAllNodesWithTag("articleListItemTitle", useUnmergedTree = true).onFirst().assertIsDisplayed().assertIsEnabled().fetchSemanticsNode().config.getOrNull(
            SemanticsProperties.Text,
        )!!.first().text

        composeTestRule.onAllNodesWithTag("articleListItem").onFirst().assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("articleDetailView").assertIsDisplayed()
        composeTestRule.onNodeWithText(articleTitle).assertIsDisplayed()

        composeTestRule.onAllNodesWithText(articleTitle).onFirst().assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `navigation to carParkDetail displays correct content`() {
        setupAppNavHost(Screens.CarParks.route) // Start at CarParks overview to make sure this test does not depend on the result of other navigation tests

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("carParkListItem"), 5000L) // wait for carParks to load

        val carParkName = composeTestRule.onAllNodesWithTag("carParkListItemTitle", useUnmergedTree = true).onFirst().assertIsDisplayed().assertIsEnabled().fetchSemanticsNode().config.getOrNull(
            SemanticsProperties.Text,
        )!!.first().text

        composeTestRule.onAllNodesWithTag("carParkListItem").onFirst().assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("carParkDetailView").assertIsDisplayed()
        composeTestRule.onAllNodesWithText(carParkName).onFirst().assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `navigation to studyLocationDetail displays correct content`() {
        setupAppNavHost(Screens.StudyLocations.route) // Start at StudyLocations overview to make sure this test does not depend on the result of other navigation tests

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("studyLocationListItem"), 5000L) // wait for studyLocations to load

        val studyLocationName = composeTestRule.onAllNodesWithTag("studyLocationListItemTitle", useUnmergedTree = true).onFirst().assertIsDisplayed().assertIsEnabled().fetchSemanticsNode().config.getOrNull(
            SemanticsProperties.Text,
        )!!.first().text

        composeTestRule.onAllNodesWithTag("studyLocationListItem").onFirst().assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("studyLocationDetailView").assertIsDisplayed()
        composeTestRule.onAllNodesWithText(studyLocationName).onFirst().assertIsDisplayed()
    }
}
