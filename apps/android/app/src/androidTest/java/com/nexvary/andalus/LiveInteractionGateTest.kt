package com.nexvary.andalus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LiveInteractionGateTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun projectCreationChangesVisibleProjectList() {
        openFirstServiceFromHome("projects")
        composeRule.onNodeWithTag("project-name").performTextInput("بيت أندلسي")
        composeRule.onNodeWithTag("project-create").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("project-item-2").assertTextContains("بيت أندلسي", substring = true)
    }

    @Test
    fun planEditorActuallyChangesWallCount() {
        openFirstServiceFromHome("plan")
        composeRule.onNodeWithTag("plan-wall-count").assertTextContains("4", substring = true)
        composeRule.onNodeWithTag("plan-add-wall").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("plan-wall-count").assertTextContains("5", substring = true)
    }

    @Test
    fun scenePatternMaterialsAndExportControlsAreLive() {
        openFirstServiceFromHome("3d")
        composeRule.onNodeWithTag("scene-state").assertTextContains("0°", substring = true)
        composeRule.onNodeWithTag("scene-rotate").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("scene-state").assertTextContains("15°", substring = true)
        backToServices()

        openServiceFromServices("patterns")
        composeRule.onNodeWithTag("pattern-1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pattern-selected").assertTextContains("ذهبي", substring = true)
        backToServices()

        openServiceFromServices("materials")
        composeRule.onNodeWithTag("material-marble-value").assertTextContains("10")
        composeRule.onNodeWithTag("material-marble-plus").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("material-marble-value").assertTextContains("11")
        backToServices()

        openServiceFromServices("exports")
        composeRule.onNodeWithTag("export-png").performClick()
        composeRule.onNodeWithTag("export-format").assertTextContains("PNG", substring = true)
        composeRule.onNodeWithTag("export-run").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("export-status").assertIsDisplayed().assertTextContains("PNG", substring = true)
    }

    @Test
    fun paletteAndArControlsChangeApplicationState() {
        composeRule.onNodeWithTag("menu-button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer-settings").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("theme-sapphire").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("palette-sapphire").assertIsDisplayed()
        composeRule.onNodeWithTag("settings-back").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-home").assertIsDisplayed()

        openFirstServiceFromHome("ar")
        composeRule.onNodeWithTag("ar-toggle").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("ar-status").assertTextContains("جاهز", substring = true)
        composeRule.onNodeWithTag("ar-scale").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("ar-scale").assertTextContains("50", substring = true)
    }

    private fun openFirstServiceFromHome(route: String) {
        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.waitForIdle()
        openServiceFromServices(route)
    }

    private fun openServiceFromServices(route: String) {
        val index = StudioRoutes.serviceIds.indexOf(route)
        composeRule.onNodeWithTag("screen-services").performScrollToIndex(index + 1)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("service-$route").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-$route").assertIsDisplayed()
    }

    private fun backToServices() {
        composeRule.onNodeWithTag("back-button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
    }
}
