package com.pietervandewalle.androidapp

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.pietervandewalle.androidapp.ui.AndroidApp
import com.pietervandewalle.androidapp.ui.navigation.Screens
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CarParksOverviewTest {
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
                AndroidApp(
                    navController = navController,
                    startDestination = Screens.CarParks.route,
                ) // Start at carParkOverview
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `pressing the map icon toggles map view and list view`() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("carParkListItem"), 5000L) // wait for carParks to load

        // List is displayed initially
        composeTestRule.onNodeWithTag("carParkList").assertIsDisplayed()
        composeTestRule.onNodeWithTag("carParkMap").assertDoesNotExist()

        // Click map icon
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.open_map_view)).assertIsDisplayed().performClick()

        // Map is displayed
        composeTestRule.onNodeWithTag("carParkList").assertDoesNotExist()
        composeTestRule.onNodeWithTag("carParkMap").assertIsDisplayed()

        // Click map icon
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.open_map_view)).assertIsDisplayed().performClick()

        // List is displayed again
        composeTestRule.onNodeWithTag("carParkList").assertIsDisplayed()
        composeTestRule.onNodeWithTag("carParkMap").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `pressing goToTop button scrolls to top`() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("carParkListItem"), 5000L) // wait for carParks to load
        val firstCarParkName = composeTestRule.onAllNodesWithTag("carParkListItemTitle", useUnmergedTree = true).onFirst().assertIsDisplayed().assertIsEnabled().fetchSemanticsNode().config.getOrNull(
            SemanticsProperties.Text,
        )!!.first().text

        composeTestRule
            .onNodeWithTag("carParkList")
            .performScrollToIndex(8) // there should be around 20 carParks

        composeTestRule.onNodeWithText(firstCarParkName).assertIsNotDisplayed()

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.scroll_to_top)).assertIsDisplayed().performClick()

        composeTestRule.onNodeWithText(firstCarParkName).assertIsDisplayed()
    }
}
