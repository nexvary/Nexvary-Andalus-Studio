package com.nexvary.andalus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

        StudioRoutes.serviceIds.forEach { route ->
            composeRule.onNodeWithTag("service-$route").performScrollTo().performClick()
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
        StudioRoutes.serviceIds.forEach { route ->
            composeRule.onNodeWithTag("service-$route").performScrollTo().performClick()
            composeRule.onNodeWithTag("screen-$route").assertIsDisplayed()
            composeRule.runOnUiThread {
                composeRule.activity.onBackPressedDispatcher.onBackPressed()
            }
            composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
        }
    }
}
