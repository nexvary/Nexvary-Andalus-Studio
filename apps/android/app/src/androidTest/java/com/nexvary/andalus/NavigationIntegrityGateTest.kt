package com.nexvary.andalus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationIntegrityGateTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun everyPrimaryAndServiceRouteOpensAndReturnsCorrectly() {
        composeRule.onNodeWithTag("screen-home").assertIsDisplayed()

        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()

        StudioRoutes.serviceIds.forEachIndexed { index, route ->
            openServiceAt(index = index, route = route)
            composeRule.onNodeWithTag("screen-$route").assertIsDisplayed()
            composeRule.onNodeWithTag("back-button").assertIsDisplayed().performClick()
            composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
        }

        composeRule.onNodeWithTag("nav-about").performClick()
        composeRule.onNodeWithTag("screen-about").assertIsDisplayed()
        listOf("social-website", "social-facebook", "social-email", "social-youtube", "social-x")
            .forEach { tag ->
                composeRule.onNodeWithTag(tag).performScrollTo().assertIsDisplayed()
            }

        composeRule.onNodeWithTag("nav-home").performClick()
        composeRule.onNodeWithTag("screen-home").assertIsDisplayed()

        composeRule.onNodeWithTag("home-start-services").performClick()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
    }

    @Test
    fun systemBackFromEveryFeatureReturnsToServicesInsteadOfDeadEnding() {
        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()

        StudioRoutes.serviceIds.forEachIndexed { index, route ->
            openServiceAt(index = index, route = route)
            composeRule.onNodeWithTag("screen-$route").assertIsDisplayed()
            composeRule.runOnUiThread {
                composeRule.activity.onBackPressedDispatcher.onBackPressed()
            }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
        }
    }

    private fun openServiceAt(index: Int, route: String) {
        // LazyColumn only composes visible children. Scroll the list itself to the
        // service index first, then interact with the now-composed route card.
        // Index 0 is the Services heading, therefore service rows start at 1.
        composeRule.onNodeWithTag("screen-services").performScrollToIndex(index + 1)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("service-$route").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
    }
}
