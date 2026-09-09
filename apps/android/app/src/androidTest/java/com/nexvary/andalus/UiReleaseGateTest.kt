package com.nexvary.andalus

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
        composeRule.onNodeWithTag("language-picker").assertTextContains("English")
        composeRule.onNodeWithTag("nav-home").assertTextContains("Home")

        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed()
        composeRule.onNodeWithTag("nav-services").assertTextContains("Services")

        composeRule.onNodeWithTag("language-picker").performClick()
        composeRule.onNodeWithTag("lang-ar").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("language-picker").assertTextContains("العربية")
        composeRule.onNodeWithTag("nav-services").assertTextContains("الخدمات")
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

    private fun overlaps(a: Rect, b: Rect): Boolean {
        return a.left < b.right && a.right > b.left && a.top < b.bottom && a.bottom > b.top
    }
}
