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

        // The Royal home screen intentionally has a tall hero and quick actions.
        // Scroll to the primary CTA before clicking instead of assuming the compact
        // pre-overhaul layout where it was always above the fold.
        composeRule.onNodeWithTag("home-start-services")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
    }

    @Test
    fun royalMenuAndSettingsAreLiveAndReturnHome() {
        composeRule.onNodeWithTag("screen-home").assertIsDisplayed()
        composeRule.onNodeWithTag("menu-button").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer-settings").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-settings").assertIsDisplayed()
        composeRule.onNodeWithTag("settings-back").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-home").assertIsDisplayed()
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
        composeRule.onNodeWithTag("screen-services").performScrollToIndex(index + 1)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("service-$route").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
    }
}
