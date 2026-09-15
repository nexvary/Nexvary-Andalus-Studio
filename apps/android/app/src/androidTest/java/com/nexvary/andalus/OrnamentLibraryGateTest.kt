package com.nexvary.andalus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrnamentLibraryGateTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun catalogContainsTwelveFamiliesWithTwoHundredUniqueAssetsEach() {
        assertEquals(2400, LocalOrnamentCatalog.size)
        val grouped = LocalOrnamentCatalog.groupBy { it.family }
        assertEquals(12, grouped.size)
        grouped.values.forEach { assets -> assertEquals(200, assets.size) }
        assertEquals(2400, LocalOrnamentCatalog.map { it.id }.toSet().size)
    }

    @Test
    fun familyTabsReplaceTheVisibleDatasetInsteadOfKeepingCalligraphyCards() {
        composeRule.onNodeWithTag("nav-services").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-services").assertIsDisplayed().performScrollToIndex(5)
        composeRule.onNodeWithTag("service-patterns").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("screen-patterns").assertIsDisplayed()

        composeRule.onNodeWithTag("ornament-filter-muqarnas").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("ornament-selected-family").assertTextContains("مقرنصات", substring = true)
        composeRule.onNodeWithTag("ornament-name-1001").performScrollTo().assertTextContains("مقرنصات 1", substring = true)

        composeRule.onNodeWithTag("ornament-filter-ceiling").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("ornament-selected-family").assertTextContains("زخارف أسقف", substring = true)
        composeRule.onNodeWithTag("ornament-name-2001").performScrollTo().assertTextContains("زخارف أسقف 1", substring = true)

        composeRule.onNodeWithTag("ornament-filter-calligraphy").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("ornament-selected-family").assertTextContains("حليات خطية", substring = true)
        composeRule.onNodeWithTag("ornament-name-2201").performScrollTo().assertTextContains("حليات خطية 1", substring = true)

        assertTrue(ornamentsForFamily("muqarnas").all { it.family == "muqarnas" })
        assertTrue(ornamentsForFamily("ceiling").all { it.family == "ceiling" })
        assertTrue(ornamentsForFamily("calligraphy").all { it.family == "calligraphy" })
    }
}
