package com.nexvary.andalus

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiReleaseGateTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun primaryNavigationTargetsAreVisibleAndDoNotOverlap() {
        val tags = listOf("nav-home", "nav-services", "nav-about")
        tags.forEach { composeRule.onNodeWithTag(it).assertIsDisplayed() }
        val bounds = tags.map { composeRule.onNodeWithTag(it).fetchSemanticsNode().boundsInRoot }
        for (i in bounds.indices) {
            for (j in i + 1 until bounds.size) {
                assertTrue("Primary navigation targets overlap", !overlaps(bounds[i], bounds[j]))
            }
        }
    }

    @Test
    fun arabicAndEnglishCopyRemainUsableInRtlAndLtr() {
        composeRule.onNodeWithTag("language-picker").performClick()
        composeRule.onNodeWithTag("lang-en").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("language-picker").assertTextContains("English", substring = true)
        composeRule.onNodeWithTag("nav-home").assertTextContains("Home", substring = true)

        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
        composeRule.onNodeWithTag("nav-services").assertTextContains("Services", substring = true)

        composeRule.onNodeWithTag("language-picker").performClick()
        composeRule.onNodeWithTag("lang-ar").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("language-picker").assertTextContains("العربية", substring = true)
        composeRule.onNodeWithTag("nav-services").assertTextContains("الخدمات", substring = true)
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
    }

    @Test
    fun aboutButtonsRemainReachableOnPhoneLayout() {
        composeRule.onNodeWithTag("nav-about").performClick()
        composeRule.onNodeWithTag("screen-about").assertIsDisplayed()
        listOf("social-website", "social-facebook", "social-email", "social-youtube", "social-x")
            .forEach { tag ->
                composeRule.onNodeWithTag(tag).performScrollTo().assertIsDisplayed()
            }
    }

    @Test
    fun colorIdentitySelectorChangesTheWholeAppTheme() {
        composeRule.onNodeWithTag("menu-button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("drawer-settings").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-settings").assertIsDisplayed()

        composeRule.onNodeWithTag("theme-sapphire").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("palette-sapphire").assertIsDisplayed()
        composeRule.onNodeWithTag("theme-current").assertTextContains("Sapphire Gold", substring = true)

        composeRule.onNodeWithTag("theme-emerald").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("palette-emerald").assertIsDisplayed()
        composeRule.onNodeWithTag("theme-current").assertTextContains("Emerald Andalus", substring = true)

        composeRule.onNodeWithTag("theme-rose").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("palette-rose").assertIsDisplayed()
    }

    @Test
    fun servicePagesShowWorkspaceInsteadOfQaPlaceholder() {
        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
        composeRule.onNodeWithTag("service-projects").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-projects").assertIsDisplayed()
        composeRule.onNodeWithTag("workspace-projects").assertIsDisplayed()
    }

    @Test
    fun planDesignerControlsMutateTheVisibleDesign() {
        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.onNodeWithTag("screen-services").performScrollToIndex(3)
        composeRule.onNodeWithTag("service-plan").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("screen-plan").assertIsDisplayed()
        composeRule.onNodeWithTag("plan-canvas").assertIsDisplayed()
        composeRule.onNodeWithTag("plan-rooms-count").assertTextContains("2", substring = true)
        composeRule.onNodeWithTag("plan-add-room").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("plan-rooms-count").assertTextContains("3", substring = true)
        composeRule.onNodeWithTag("plan-add-arch").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("plan-arches-count").assertTextContains("2", substring = true)
    }

    @Test
    fun threeDStudioControlsMutateSceneState() {
        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.onNodeWithTag("screen-services").performScrollToIndex(4)
        composeRule.onNodeWithTag("service-3d").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("screen-3d").assertIsDisplayed()
        composeRule.onNodeWithTag("3d-canvas").assertIsDisplayed()
        composeRule.onNodeWithTag("3d-angle").assertTextContains("30", substring = true)
        composeRule.onNodeWithTag("3d-rotate").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("3d-angle").assertTextContains("45", substring = true)
    }

    @Test
    fun projectAndExportActionsAreNotDeadControls() {
        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.onNodeWithTag("service-projects").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("projects-count").assertTextContains("1", substring = true)
        composeRule.onNodeWithTag("projects-create").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("projects-count").assertTextContains("2", substring = true)

        composeRule.onNodeWithTag("back-button").performClick()
        composeRule.onNodeWithTag("screen-services").performScrollToIndex(8)
        composeRule.onNodeWithTag("service-exports").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exports-generate").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exports-status").assertTextContains("#1", substring = true)
    }

    private fun overlaps(a: Rect, b: Rect): Boolean {
        return a.left < b.right && a.right > b.left && a.top < b.bottom && a.bottom > b.top
    }
}
